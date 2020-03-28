package com.server.routes;

import com.server.controllers.AccountController;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class AccountRouter extends Router {

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    super.handle(httpExchange);
    String methodAddress =
      httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI();
    switch (methodAddress) {
      case "POST /accounts/createAccount":
        routeCreateAccount();
        break;
      case "GET /accounts/getbanks":
        routeGetBanks();
        break;
    }
  }

  private void routeGetBanks(){
    Response response = AccountController.controllerRetrieveBanks();
    sendResponse(response);
  }

  private void routeCreateAccount() {
    if (authorization.getIsValid()) {
      Response response = AccountController.controllerCreateAccount(
        authorization
      );
      sendResponse(response);
    } else {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
    }
  }
}
