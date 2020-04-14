package com.server.controllers;

import com.server.authentication.Token;
import com.server.containers.Account;
import com.server.containers.Card;
import com.server.database.CardDatabase;
import com.server.routes.Response;
import com.server.routes.Router;
import java.sql.SQLException;
import java.util.ArrayList;

public class CardController extends Controller {

  public static Response controllerCreateCard(Card newCard) {
    // If no limits are given set default
    newCard.spendingLimit =
      (newCard.spendingLimit != null) ? newCard.spendingLimit : 0;
    newCard.withdrawLimit =
      (newCard.withdrawLimit != null) ? newCard.withdrawLimit : 0;
    newCard.area = (newCard.area != null) ? newCard.area : "";
    try {
      Integer cardId = CardDatabase.insertCard(
        newCard.accountId,
        newCard.spendingLimit,
        newCard.withdrawLimit,
        newCard.area
      );
      Card returnValues = newCard;
      returnValues.cardId = cardId;
      return new Response(201, returnValues, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerGetCards(
    Account account,
    Token authorization
  ) {
    try {
      if (!userOwnsAccount(account.accountId, authorization.userId)) {
        return new Response(
          401,
          Router.AUTHENTICATION_ERROR,
          Response.ResponseType.TEXT
        );
      }

      ArrayList<Card> cards = CardDatabase.retrieveCards(account.accountId);
      return new Response(200, cards, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }
}
