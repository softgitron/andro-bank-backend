package com.server.routes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Router implements HttpHandler {
  protected final String API_PARAMETER_ERROR =
    "Some of the parameters didn't stisfy contraint. Check API documentation.";

  // This should be alway updated from inherited classes
  protected HttpExchange httpExchange;

  protected Object decodeJson(Class toClass) {
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
      //TODO: handle exception
    }
    String jsonString = jsonBuilder.toString();
    Gson gson = new Gson();
    Object results = null;
    try {
      results = gson.fromJson(jsonString, toClass);
    } catch (JsonSyntaxException e) {
      //TODO: handle json exceptions
    }
    return results;
  }

  protected void sendResponse(Response response) {
    executeResponse(response);
  }

  protected void sendResponse(
    Integer responseCode,
    String responseData,
    Response.ResponseType responseType
  ) {
    executeResponse(new Response(responseCode, responseData, responseType));
  }

  private void executeResponse(Response response) {
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
        response.responseData.length()
      );
      outputStream.write(response.responseData.getBytes());
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      System.out.println("Unable to send response!");
      System.out.println(e.getMessage());
    }
  }
}
