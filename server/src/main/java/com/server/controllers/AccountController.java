package com.server.controllers;

import com.server.authentication.Token;
import com.server.containers.Account;
import com.server.containers.Bank;
import com.server.database.AccountDatabase;
import com.server.database.TransactionDatabase;
import com.server.routes.Response;
import com.server.routes.Router;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class AccountController extends Controller {

  public static Response controllerRetrieveBanks() {
    try {
      ArrayList<Bank> banks = AccountDatabase.retrieveBanks();
      return new Response(200, banks, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerCreateAccount(Token authorization) {
    // Let's generate new iban
    Random r = new Random();
    String iban = String.format(
      "FI%02d %04d %04d %04d %02d",
      r.nextInt(99),
      r.nextInt(9999),
      r.nextInt(9999),
      r.nextInt(9999),
      r.nextInt(99)
    );

    try {
      Integer accountId = AccountDatabase.insertAccount(
        authorization.userId,
        iban
      );
      Account returnValues = new Account();
      returnValues.accountId = accountId;
      returnValues.iban = iban;
      returnValues.balance = 0;
      return new Response(201, returnValues, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerGetAccounts(Token authorization) {
    try {
      ArrayList<Account> accounts = AccountDatabase.retrieveAccounts(
        authorization.userId
      );
      return new Response(200, accounts, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerAddBalance(
    Account newAccount,
    Token authorization
  ) {
    try {
      // Get information from current account
      ArrayList<Account> results = AccountDatabase.retrieveAccounts(
        authorization.userId,
        newAccount.accountId
      );
      if (results.size() != 1) {
        return new Response(
          401,
          Router.AUTHENTICATION_ERROR,
          Response.ResponseType.TEXT
        );
      }
      Account account = results.get(0);

      // Add balance to account
      account.balance += newAccount.balance;

      AccountDatabase.updateBalance(
        account.accountId,
        account.balance,
        authorization.userId
      );

      // Add details to transaction table
      TransactionDatabase.insertTransaction(
        null,
        account.accountId,
        newAccount.balance,
        TransactionDatabase.TransactionType.DepWit
      );

      return new Response(200, account, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }
}
