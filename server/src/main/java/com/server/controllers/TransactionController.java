package com.server.controllers;

import com.server.authentication.Token;
import com.server.containers.Account;
import com.server.containers.Transaction;
import com.server.database.TransactionDatabase;
import com.server.routes.Response;
import com.server.routes.Router;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionController extends Controller {

  public static Response controllerGetTransactions(
    Account account,
    Token authorization
  ) {
    if (!userOwnsAccount(account.accountId, authorization.userId)) {
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
}
