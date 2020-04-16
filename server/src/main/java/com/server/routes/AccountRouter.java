package com.server.routes;

import com.server.containers.Account;
import com.server.containers.FutureTransaction;
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
      case "POST /accounts/futureTransfer":
        routeFutureTransfer();
        break;
      case "PATCH /accounts/updateType":
        routeUpdateType();
        break;
      default:
        sendResponse(400, BAD_PATH, Response.ResponseType.TEXT);
        break;
    }
  }

  /**
   * @api {get} /accounts/getBanks Get all available banks
   * @apiVersion 1.0.0
   * @apiName getBanks
   * @apiGroup Bank
   *
   *
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * [{"bankId":0,"name":"Deals","bic":"DEALFIHH"},
   * {"bankId":1,"name":"Bear bank","bic":"BEARFIHH"},
   * {"bankId":2,"name":"Our bank","bic":"OURBFIHH"}]
   *
   */
  private void routeGetBanks() {
    Response response = AccountController.controllerRetrieveBanks();
    sendResponse(response);
  }

  /**
   * @api {post} /accounts/createAccount Create new account
   * @apiVersion 1.0.0
   * @apiName createAccount
   * @apiGroup Account
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 201 OK
   * {"accountId":1,"iban":"FI02 4597 4268 1567 54","balance":0}
   *
   */
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

  /**
   * @api {get} /accounts/getAccounts Gets all accounts
   * @apiVersion 1.0.0
   * @apiName getAccounts
   * @apiGroup Account
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * [{"accountId":1,"iban":"FI02 4597 4268 1567 54","balance":0,"type":"Normal"},
   * {"accountId":2,"iban":"FI58 4897 1864 8648 45","balance":0,"type":"Savings"}]
   *
   */
  private void routeGetAccounts() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }
    Response response = AccountController.controllerGetAccounts(authorization);
    sendResponse(response);
  }

  /**
   * @api {post} /accounts/deposit Deposit money to account
   * @apiVersion 1.0.0
   * @apiName deposit
   * @apiGroup Account
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} accountId Account where money will be saved
   * @apiParam {Number{1..}} balance How much will be added to account
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * {"accountId":1,"iban":"FI02 4597 4268 1567 54","balance":0,"type":"Normal"}
   *
   */
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

  /**
   * @api {post} /accounts/transfer Transfer from one account to another
   * @apiVersion 1.0.0
   * @apiName transfer
   * @apiGroup Account
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} fromAccountId From whitch account
   * @apiParam {String{22}} toAccountIban To what account
   * @apiParam {Number{0..}} amount Amount that should be tranferred
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * {"accountId":1,"iban":"FI02 4597 4268 1567 54","balance":100,"type":"Normal"}
   *
   */
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

  /**
   * @api {post} /accounts/futureTransfer Add new future transfer or periodic transfer
   * @apiVersion 1.0.0
   * @apiName futureTransfer
   * @apiGroup Account
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} fromAccountId From whitch account
   * @apiParam {String{22}} toAccountIban To what account
   * @apiParam {Number{1..}} amount Amount that should be tranferred
   * @apiParam {Number{1..}} [atInterval=null] How often in minutes transaction should occur
   * @apiParam {Number{1..}} [times=null] How many times trnsfer should occur
   * @apiParam {Date} atTime When transaction should occur (for the first time)
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 201 OK
   * OK
   *
   */
  private void routeFutureTransfer() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }

    FutureTransaction futureTransfer;
    try {
      futureTransfer = (FutureTransaction) decodeJson(FutureTransaction.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    if (
      futureTransfer.fromAccountId == null ||
      futureTransfer.toAccountIban == null ||
      futureTransfer.amount == null ||
      futureTransfer.atTime == null
    ) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Response response = AccountController.controllerFutureTransfer(
      futureTransfer,
      authorization
    );
    sendResponse(response);
  }

  /**
   * @api {patch} /accounts/updateType Update type of the account
   * @apiVersion 1.0.0
   * @apiName updateType
   * @apiGroup Account
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} accountId From whitch account
   * @apiParam {String{6..7}} type To what account
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * {"accountId":1,"iban":"FI02 4597 4268 1567 54","balance":100,"type":"Credit"}
   *
   */
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
