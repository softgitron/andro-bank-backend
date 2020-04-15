package com.server.controllers;

import com.server.authentication.Token;
import com.server.containers.Account;
import com.server.containers.Card;
import com.server.containers.Transaction;
import com.server.containers.Transaction.TransactionType;
import com.server.database.AccountDatabase;
import com.server.database.CardDatabase;
import com.server.database.TransactionDatabase;
import com.server.routes.Response;
import com.server.routes.Router;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class CardController extends Controller {

  public static Response controllerCreateCard(Card newCard) {
    // If no limits are given set default
    newCard.spendingLimit =
      (newCard.spendingLimit != null) ? newCard.spendingLimit : 0;
    newCard.withdrawLimit =
      (newCard.withdrawLimit != null) ? newCard.withdrawLimit : 0;
    newCard.area = (newCard.area != null) ? newCard.area : "";

    // Let's generate new card number
    Random r = new Random();
    String cardNumber = String.format(
      "%04d %04d %04d %04d",
      r.nextInt(9999),
      r.nextInt(9999),
      r.nextInt(9999),
      r.nextInt(9999)
    );
    newCard.cardNumber = cardNumber;

    try {
      Integer cardId = CardDatabase.insertCard(
        newCard.cardNumber,
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
      if (userOwnsAccount(account.accountId, authorization.userId) == null) {
        return new Response(
          401,
          Router.AUTHENTICATION_ERROR,
          Response.ResponseType.TEXT
        );
      }

      ArrayList<Card> cards = CardDatabase.retrieveCardsByAccountId(
        account.accountId
      );
      return new Response(200, cards, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerWithdraw(
    Transaction transaction,
    Token authorization
  ) {
    // Check that limits on the card are sufficient
    Card card;
    try {
      ArrayList<Card> cards = CardDatabase.retrieveCardsByCardId(
        transaction.cardId
      );
      if (cards.size() != 1) {
        return new Response(
          400,
          Router.API_PARAMETER_ERROR,
          Response.ResponseType.TEXT
        );
      }
      card = cards.get(0);
      if (card.withdrawLimit != 0 && transaction.amount > card.withdrawLimit) {
        return new Response(
          401,
          "Withdraw limit is too small.",
          Response.ResponseType.TEXT
        );
      }
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
    return executeWithdraw(
      transaction,
      authorization,
      card,
      TransactionType.Withdraw
    );
  }

  public static Response controllerPayment(
    Transaction transaction,
    Token authorization
  ) {
    // Check that limits on the card are sufficient
    Card card;
    try {
      ArrayList<Card> cards = CardDatabase.retrieveCardsByCardId(
        transaction.cardId
      );
      if (cards.size() != 1) {
        return new Response(
          400,
          Router.API_PARAMETER_ERROR,
          Response.ResponseType.TEXT
        );
      }
      card = cards.get(0);
      if (card.spendingLimit != 0 && transaction.amount > card.spendingLimit) {
        return new Response(
          401,
          "Spending limit is too small.",
          Response.ResponseType.TEXT
        );
      }
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
    return executeWithdraw(
      transaction,
      authorization,
      card,
      TransactionType.Payment
    );
  }

  private static Response executeWithdraw(
    Transaction transaction,
    Token authorization,
    Card card,
    TransactionType transactionType
  ) {
    Account account = userOwnsAccount(card.accountId, authorization.userId);
    if (account == null) {
      return new Response(
        401,
        Router.AUTHENTICATION_ERROR,
        Response.ResponseType.TEXT
      );
    }

    // Check that there is enough balance and account type is correct
    if (!withdrawCanBeMade(account, transaction.amount, transactionType)) {
      return new Response(
        401,
        Router.AUTHENTICATION_ERROR,
        Response.ResponseType.TEXT
      );
    }

    account.balance = account.balance - transaction.amount;
    try {
      // Execute withdrawal
      AccountDatabase.updateBalance(
        account.accountId,
        account.balance,
        authorization.userId
      );

      // Write details of the withdrawal to transactions.
      TransactionDatabase.insertTransaction(
        account.accountId,
        null,
        card.cardId,
        transaction.amount,
        transactionType
      );
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
    return new Response(200, account, Response.ResponseType.JSON);
  }
}
