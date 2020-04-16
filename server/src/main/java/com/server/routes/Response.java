package com.server.routes;

public class Response {

  // Class for keeping response that can be send to client
  public enum ResponseType {
    TEXT,
    JSON
  }

  public Integer responseCode;
  public Object responseData;
  public String responseToken;
  public ResponseType responseType;

  public Response(
    Integer responseCode,
    Object responseData,
    ResponseType responseType
  ) {
    this.responseCode = responseCode;
    this.responseData = responseData;
    this.responseType = responseType;
    this.responseToken = null;
  }

  public Response(
    Integer responseCode,
    Object responseData,
    ResponseType responseType,
    String responseToken
  ) {
    this.responseCode = responseCode;
    this.responseData = responseData;
    this.responseType = responseType;
    this.responseToken = responseToken;
  }
}
