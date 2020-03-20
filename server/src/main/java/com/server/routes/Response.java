package com.server.routes;

public class Response {

  public enum ResponseType {
    TEXT,
    JSON
  }

  public Integer responseCode;
  public String responseData;
  public String responseToken;
  public ResponseType responseType;

  public Response(
    Integer responseCode,
    String responseData,
    ResponseType responseType
  ) {
    this.responseCode = responseCode;
    this.responseData = responseData;
    this.responseType = responseType;
    this.responseToken = null;
  }

  public Response(
    Integer responseCode,
    String responseData,
    ResponseType responseType,
    String responseToken
  ) {
    this.responseCode = responseCode;
    this.responseData = responseData;
    this.responseType = responseType;
    this.responseToken = responseToken;
  }
}
