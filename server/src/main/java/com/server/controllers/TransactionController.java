package com.server.controllers;

import com.server.authentication.Token;
import com.server.containers.Account;
import com.server.containers.FutureTransaction;
import com.server.containers.Transaction;
import com.server.database.TransactionDatabase;
import com.server.routes.Response;
import com.server.routes.Router;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionController extends Controller {

  // Retrieves all transactions for sepcific account
  // Returns list of transactions
  public static Response controllerGetTransactions(
    Account account,
    Token authorization
  ) {
    if (userOwnsAccount(account.accountId, authorization.userId) == null) {
      return new Response(
        401,
        Router.AUTHENTICATION_ERROR,
        Response.ResponseType.TEXT
      );
    }
    try {
      ArrayList<Transaction> transactions = TransactionDatabase.retrieveTransactions(
        account.accountId
      );
      return new Response(200, transactions, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  // Retrieves all future transactions for specific account
  // Returns list of the future transactions
  public static Response controllerGetFutureTransactions(
    Account account,
    Token authorization
  ) {
    if (userOwnsAccount(account.accountId, authorization.userId) == null) {
      return new Response(
        401,
        Router.AUTHENTICATION_ERROR,
        Response.ResponseType.TEXT
      );
    }
    try {
      ArrayList<FutureTransaction> transactions = TransactionDatabase.retrieveFutureTransactions(
        account.accountId
      );
      return new Response(200, transactions, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  // Deletes future transaction
  // Returns OK if execution was succesfull
  public static Response controllerDeleteFutureTransaction(
    FutureTransaction futureTransaction,
    Token authorization
  ) {
    if (
      userOwnsAccount(futureTransaction.fromAccountId, authorization.userId) ==
      null
    ) {
      return new Response(
        401,
        Router.AUTHENTICATION_ERROR,
        Response.ResponseType.TEXT
      );
    }
    try {
      TransactionDatabase.deleteFutureTransaction(
        futureTransaction.futureTransferId
      );
      return new Response(200, "OK", Response.ResponseType.TEXT);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }
}
