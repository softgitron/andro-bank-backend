package com.server.routes;

import com.server.containers.Account;
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
      case "GET /accounts/getAccounts":
        routeGetAccounts();
        break;
      case "POST /accounts/addBalance":
        routeAddBalance();
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
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }
    Response response = AccountController.controllerCreateAccount(
      authorization
    );
    sendResponse(response);
  }

  private void routeGetAccounts() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }
    Response response = AccountController.controllerGetAccounts(authorization);
    sendResponse(response);
  }

  private void routeAddBalance() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Account account;
    try {
      account = (Account) decodeJson(Account.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    if (account.accountId == null || account.balance == null) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Response response = AccountController.controllerAddBalance(
      account,
      authorization
    );
    sendResponse(response);
  }
}
