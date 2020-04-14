package com.server.routes;

import com.server.controllers.AccountController;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class AccountRouter extends Router {

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    super.handle(httpExchange);
    switch (methodAddress) {
      case "GET /accounts/getBanks":
        routeGetBanks();
        break;
      case "POST /accounts/createAccount":
        routeCreateAccount();
        break;
      case "POST /accounts/getAccounts":
        routeGetAccounts();
        break;
      default:
        sendResponse(400, BAD_PATH, Response.ResponseType.TEXT);
        break;
    }
  }

  private void routeGetBanks() {
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

  private void routeGetAccounts() {
    if (authorization.getIsValid()) {
      Response response = AccountController.controllerGetAccounts(
        authorization
      );
      sendResponse(response);
    } else {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
    }
  }
}
