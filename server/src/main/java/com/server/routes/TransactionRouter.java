package com.server.routes;

import com.server.containers.Account;
import com.server.controllers.TransactionController;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class TransactionRouter extends Router {

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    super.handle(httpExchange);
    switch (methodAddress) {
      case "GET /transactions/getTransactions":
        routeGetTransactions();
        break;
      default:
        sendResponse(400, BAD_PATH, Response.ResponseType.TEXT);
        break;
    }
  }

  private void routeGetTransactions() {
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

    if (account.accountId == null) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Response response = TransactionController.controllerGetTransactions(
      account,
      authorization
    );
    sendResponse(response);
  }
}
