package com.server.routes;

import com.server.containers.Account;
import com.server.containers.Transaction;
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
      case "POST /accounts/deposit":
        routeDeposit();
        break;
      case "POST /accounts/transfer":
        routeTransfer();
        break;
      case "PATCH /accounts/updateType":
        routeUpdateType();
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

    Account account;
    try {
      account = (Account) decodeJson(Account.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    if (account.type == null) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Response response = AccountController.controllerCreateAccount(
      account,
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

  private void routeDeposit() {
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

    Response response = AccountController.controllerDeposit(
      account,
      authorization
    );
    sendResponse(response);
  }

  private void routeTransfer() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Transaction transaction;
    try {
      transaction = (Transaction) decodeJson(Transaction.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    if (
      transaction.fromAccountId == null ||
      transaction.toAccountIban == null ||
      transaction.amount == null ||
      transaction.amount < 1
    ) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Response response = AccountController.controllerTransfer(
      transaction,
      authorization
    );
    sendResponse(response);
  }

  private void routeUpdateType() {
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

    if (account.accountId == null || account.type == null) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Response response = AccountController.controllerUpdateType(
      account,
      authorization
    );
    sendResponse(response);
  }
}
