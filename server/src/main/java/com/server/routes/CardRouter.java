package com.server.routes;

import com.server.containers.Account;
import com.server.containers.Card;
import com.server.containers.Transaction;
import com.server.containers.Transaction.TransactionType;
import com.server.controllers.CardController;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class CardRouter extends Router {
  private final String AREA_REGEX = "^FI|SWE|GB|$";

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    super.handle(httpExchange);
    switch (methodAddress) {
      case "POST /cards/createCard":
        routeCreateCard();
        break;
      case "POST /cards/getCards":
        routeGetCards();
        break;
      case "POST /cards/withdraw":
        routeUseMoney(TransactionType.Withdraw);
        break;
      case "POST /cards/payment":
        routeUseMoney(TransactionType.Payment);
        break;
      case "PATCH /cards/updateCard":
        routeUpdateCard();
        break;
      default:
        sendResponse(400, BAD_PATH, Response.ResponseType.TEXT);
        break;
    }
  }

  /**
   * @api {post} /cards/createCards Create new card for the account
   * @apiVersion 1.0.0
   * @apiName createCards
   * @apiGroup Card
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} accountId Id of the account where card is attached to
   * @apiParam {Number{0..}} [withdrawLimit=0] Limit of the withdrawals (0 means no limit)
   * @apiParam {Number{0..}} [paymnetLimit=0] Limit of the payments (0 means no limit)
   * @apiParam {String} [area=""] Area where payments can be only processed ("" means no limit)
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 201 OK
   * {"cardId":1,"cardNumber":"1025 5879 5483 2858","accountId":1,"withdrawLimit":0,"spendingLimit":0,"area":""}
   *
   */
  private void routeCreateCard() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Card newCard;
    try {
      newCard = (Card) decodeJson(Card.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    if (
      newCard.accountId != null &&
      (newCard.area == null || newCard.area.matches(AREA_REGEX))
    ) {
      Response response = CardController.controllerCreateCard(newCard);
      sendResponse(response);
    } else {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
    }
  }

  /**
   * @api {post} /cards/getCards Get cards that are attached to account
   * @apiVersion 1.0.0
   * @apiName getCards
   * @apiGroup Card
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} accountId Id of the account where cards are attached to.
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * [{"cardId":1,"cardNumber":"1025 5879 5483 2858","accountId":1,"withdrawLimit":0,"spendingLimit":0,"area":""}]
   *
   */
  private void routeGetCards() {
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

    if (account.accountId != null) {
      Response response = CardController.controllerGetCards(
        account,
        authorization
      );
      sendResponse(response);
    } else {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
    }
  }

  /**
   * @api {post} /cards/withdraw Make withdraw using card
   * @apiVersion 1.0.0
   * @apiName withdraw
   * @apiGroup Card
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} cardId Id of the card that is used.
   * @apiParam {Number{0..}} amount Amount that is withdrawn from the account.
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * {"accountId":1,"iban":"FI02 4597 4268 1567 54","balance":100,"type":"Credit"}
   *
   */
  /**
   * @api {post} /cards/payment Make payment using card
   * @apiVersion 1.0.0
   * @apiName payment
   * @apiGroup Card
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} cardId Id of the card that is used.
   * @apiParam {Number{0..}} amount Amount that is payed from the account.
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * {"accountId":1,"iban":"FI02 4597 4268 1567 54","balance":100,"type":"Credit"}
   *
   */
  private void routeUseMoney(TransactionType type) {
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

    if (transaction.cardId == null || transaction.amount == null) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }
    Response response;
    if (type == TransactionType.Withdraw) {
      response = CardController.controllerWithdraw(transaction, authorization);
    } else {
      response = CardController.controllerPayment(transaction, authorization);
    }
    sendResponse(response);
  }

  /**
   * @api {post} /cards/updateCard Update details of the card
   * @apiVersion 1.0.0
   * @apiName updateCard
   * @apiGroup Card
   *
   * @apiHeader {String} x-access-token authentication token of the session.
   *
   * @apiParam {Number{0..}} cardId Id of the card that is being updated
   * @apiParam {Number{0..}} [withdrawLimit=0] Limit of the withdrawals (0 means no limit)
   * @apiParam {Number{0..}} [paymnetLimit=0] Limit of the payments (0 means no limit)
   * @apiParam {String} [area=""] Area where payments can be only processed ("" means no limit)
   *
   * @apiSuccessExample Success-Response:
   *     HTTP/1.1 200 OK
   * {"cardId":1,"cardNumber":"1025 5879 5483 2858","accountId":1,"withdrawLimit":0,"spendingLimit":0,"area":""}
   *
   */
  private void routeUpdateCard() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }

    Card card;
    try {
      card = (Card) decodeJson(Card.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    if (card.cardId == null) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }
    Response response = CardController.controllerUpdateCard(
      card,
      authorization
    );
    sendResponse(response);
  }
}
