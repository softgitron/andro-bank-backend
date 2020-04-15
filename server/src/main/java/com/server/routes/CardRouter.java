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
      case "GET /cards/getCards":
        routeGetCards();
        break;
      case "POST /cards/withdraw":
        routeUserMoney(TransactionType.Withdraw);
        break;
      case "POST /cards/payment":
        routeUserMoney(TransactionType.Payment);
        break;
      default:
        sendResponse(400, BAD_PATH, Response.ResponseType.TEXT);
        break;
    }
  }

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

    if (newCard.area == null || newCard.area.matches(AREA_REGEX)) {
      Response response = CardController.controllerCreateCard(newCard);
      sendResponse(response);
    } else {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
    }
  }

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

  private void routeUserMoney(TransactionType type) {
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
}
