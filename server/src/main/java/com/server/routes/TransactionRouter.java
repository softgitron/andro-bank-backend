package com.server.routes;

import com.server.containers.Account;
import com.server.containers.FutureTransaction;
import com.server.controllers.TransactionController;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class TransactionRouter extends Router {

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    super.handle(httpExchange);
    switch (methodAddress) {
      case "POST /transactions/getTransactions":
        routeGetTransactions();
        break;
      case "POST /transactions/getFutureTransactions":
        routeGetFutureTransactions();
        break;
      case "DELETE /transactions/deleteFutureTransaction":
        routeDeleteFutureTransaction();
        break;
      default:
        sendResponse(400, BAD_PATH, Response.ResponseType.TEXT);
        break;
    }
  }

  /**
   * @api {post} /transactions/getTransactions Get transactions of the account
   * @apiVersion 1.0.0
   * @apiName getTransactions
   * @apiGroup Transaction
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} accountId Id of the account where transactions are retrieved
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * [{"toAccountId":1,"toAccountIban":"FI24 5864 8568 9554 87","toAccountBic":"DEALFIHH",
   * "amount":50000,"time":"1.5.2020 15:41 PM","type":"Deposit"},
   * {"fromAccountId":2,"fromAccountIban":"FI58 9348 5687 5324 67","fromAccountBic":"DEALFIHH",
   * "cardId":1,"cardNumber":"1254 8658 9425 7896","amount":10000,"time":"1.5.2020 18:45 PM","type":"Withdraw"},
   * {"fromAccountId":3,"fromAccountIban":"FI98 2357 8654 1598 65","fromAccountBic":"DEALFIHH",
   * "cardId":1,"cardNumber":"1254 8658 9425 7896","amount":10000,"time":"1.5.2020 20:50 PM","type":"Payment"}]
   *
   */
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

  /**
   * @api {post} /transactions/getFutureTransactions Get future or periodic transactions
   * @apiVersion 1.0.0
   * @apiName getFutureTransactions
   * @apiGroup Transaction
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} accountId Id of the account where futureTransactions are retrieved
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * [{"toAccountId":1,"toAccountIban":"FI24 5864 8568 9554 87","toAccountBic":"DEALFIHH",
   * "amount":50000,"time":"1.5.2020 15:41 PM","type":"Deposit"},
   * {"fromAccountId":2,"fromAccountIban":"FI58 9348 5687 5324 67","fromAccountBic":"DEALFIHH",
   * "cardId":1,"cardNumber":"1254 8658 9425 7896","amount":10000,"time":"1.5.2020 18:45 PM","type":"Withdraw"},
   * {"fromAccountId":3,"fromAccountIban":"FI98 2357 8654 1598 65","fromAccountBic":"DEALFIHH",
   * "cardId":1,"cardNumber":"1254 8658 9425 7896","amount":10000,"time":"1.5.2020 20:50 PM","type":"Payment"}]
   *
   */
  private void routeGetFutureTransactions() {
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

    Response response = TransactionController.controllerGetFutureTransactions(
      account,
      authorization
    );
    sendResponse(response);
  }

  /**
   * @api {delete} /transactions/deleteFutureTransaction Remove future transaction
   * @apiVersion 1.0.0
   * @apiName deleteFutureTransaction
   * @apiGroup Transaction
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} futureTransferId Id of the future transaction to be removed
   * @apiParam {Number{0..}} fromAccountId ID account that future transaction is related to
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * OK
   *
   */
  private void routeDeleteFutureTransaction() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }

    FutureTransaction futureTransaction;
    try {
      futureTransaction =
        (FutureTransaction) decodeJson(FutureTransaction.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    if (
      futureTransaction.futureTransferId == null ||
      futureTransaction.fromAccountId == null
    ) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Response response = TransactionController.controllerDeleteFutureTransaction(
      futureTransaction,
      authorization
    );
    sendResponse(response);
  }
}
