package com.server.routes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.server.authentication.Authentication;
import com.server.authentication.Token;
import com.sun.net.httpserver.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Router implements HttpHandler {
  public static final String API_PARAMETER_ERROR =
    "Some of the parameters didn't satisfy contraint. Check API documentation.";
  public static final String BAD_PATH =
    "Path does not exist. Check that you have entered correct details.";
  public static final String AUTHENTICATION_ERROR =
    "Authentication is invalid.";

  protected HttpExchange httpExchange;
  protected Token authorization;
  protected String methodAddress;

  // Must be alway called from inherited classes
  // Does basic preparations for routing like authentication
  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    this.httpExchange = httpExchange;
    this.methodAddress =
      httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI();
    Headers headers = httpExchange.getRequestHeaders();
    if (headers.containsKey("X-Auth-Token")) {
      String rawToken = headers.get("X-Auth-Token").get(0);
      authorization = Authentication.validateJWT(rawToken);
    } else {
      authorization = new Token(0);
      authorization.setIsValid(false);
    }
  }

  // Decodes json based on the class type
  // Returns Decoded object
  protected Object decodeJson(Class toClass) throws Exception {
    InputStream inputStream = httpExchange.getRequestBody();
    StringBuilder jsonBuilder = new StringBuilder();
    String jsonLine;
    InputStreamReader streamReader = new InputStreamReader(inputStream);
    BufferedReader br = new BufferedReader(streamReader);
    try {
      while ((jsonLine = br.readLine()) != null) {
        jsonBuilder.append(jsonLine.trim());
      }
    } catch (IOException e) {
      sendResponse(
        500,
        "IOException while receiving.",
        Response.ResponseType.TEXT
      );
      throw new Exception("IOException while receiving");
    }
    String jsonString = jsonBuilder.toString();
    Gson gson = new Gson();
    Object results = null;
    try {
      results = gson.fromJson(jsonString, toClass);
    } catch (JsonSyntaxException e) {
      sendResponse(400, "Invalid JSON syntax.", Response.ResponseType.TEXT);
      throw new Exception("Invalid JSON syntax.");
    }
    return results;
  }

  // Sends response that is wrapped inside respose object
  protected void sendResponse(Response response) {
    executeResponse(response);
  }

  // Sends response that is manually made
  protected void sendResponse(
    Integer responseCode,
    String responseData,
    Response.ResponseType responseType
  ) {
    executeResponse(new Response(responseCode, responseData, responseType));
  }

  // Sends response back to client
  private void executeResponse(Response response) {
    // Check data type
    String responseData;
    if (response.responseData instanceof String) {
      responseData = (String) response.responseData;
    } else {
      Gson gson = new Gson();
      responseData = gson.toJson(response.responseData);
    }
    List<String> type = new ArrayList<String>();
    switch (response.responseType) {
      case TEXT:
        type.add("text/html");
        break;
      case JSON:
        type.add("application/json");
        break;
    }
    httpExchange.getResponseHeaders().put("Content-Type", type);

    // Check is there new authentication to be set
    if (response.responseToken != null) {
      List<String> token = new ArrayList<String>();
      token.add(response.responseToken);
      httpExchange.getResponseHeaders().put("X-Auth-Token", token);
    }

    try {
      OutputStream outputStream = httpExchange.getResponseBody();
      httpExchange.sendResponseHeaders(
        response.responseCode,
        responseData.length()
      );
      outputStream.write(responseData.getBytes());
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      System.out.println("Unable to send response!");
      System.out.println(e.getMessage());
    }
  }
}
