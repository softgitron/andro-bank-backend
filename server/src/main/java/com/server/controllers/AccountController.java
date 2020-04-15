package com.server.controllers;

import com.server.authentication.Token;
import com.server.containers.Account;
import com.server.containers.Account.AccountType;
import com.server.containers.Bank;
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

public class AccountController extends Controller {

  public static Response controllerRetrieveBanks() {
    try {
      ArrayList<Bank> banks = AccountDatabase.retrieveBanks();
      return new Response(200, banks, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerCreateAccount(
    Account account,
    Token authorization
  ) {
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
        iban,
        account.type
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

  public static Response controllerDeposit(
    Account newAccount,
    Token authorization
  ) {
    try {
      Account account = userOwnsAccount(
        newAccount.accountId,
        authorization.userId
      );
      if (account == null) {
        return new Response(
          401,
          Router.AUTHENTICATION_ERROR,
          Response.ResponseType.TEXT
        );
      }

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
        null,
        newAccount.balance,
        Transaction.TransactionType.Deposit
      );

      return new Response(200, account, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerTransfer(
    Transaction transaction,
    Token authorization
  ) {
    // Get foreign account using iban
    try {
      // Get information from current account
      ArrayList<Account> results = AccountDatabase.retrieveAccounts(
        transaction.toAccountIban
      );
      if (results.size() != 1) {
        return new Response(
          401,
          Router.AUTHENTICATION_ERROR,
          Response.ResponseType.TEXT
        );
      }
      Account toAccount = results.get(0);

      Account fromAccount = userOwnsAccount(
        transaction.fromAccountId,
        authorization.userId
      );
      if (
        !withdrawCanBeMade(
          fromAccount,
          toAccount.accountId,
          authorization.userId,
          transaction.amount,
          TransactionType.Transfer
        )
      ) {
        return new Response(
          401,
          Router.AUTHENTICATION_ERROR,
          Response.ResponseType.TEXT
        );
      }

      // Execute transfer
      // Calculate balance
      fromAccount.balance -= transaction.amount;
      toAccount.balance += transaction.amount;
      AccountDatabase.updateBalance(
        fromAccount.accountId,
        fromAccount.balance,
        authorization.userId
      );
      AccountDatabase.updateBalance(
        toAccount.accountId,
        toAccount.balance,
        authorization.userId
      );

      // Log transaction
      TransactionDatabase.insertTransaction(
        fromAccount.accountId,
        toAccount.accountId,
        null,
        transaction.amount,
        TransactionType.Transfer
      );
      return new Response(200, fromAccount, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerUpdateType(
    Account newAccount,
    Token authorization
  ) {
    try {
      Account account = userOwnsAccount(
        newAccount.accountId,
        authorization.userId
      );
      if (account == null) {
        return new Response(
          401,
          Router.AUTHENTICATION_ERROR,
          Response.ResponseType.TEXT
        );
      }

      // Check that there is no cards attached to account if new type is "Savings"
      if (newAccount.type == AccountType.Savings) {
        if (
          CardDatabase.retrieveCardsByAccountId(account.accountId).size() != 0
        ) {
          return new Response(
            400,
            "There is cards attached to the account. Can't update to savings type.",
            Response.ResponseType.TEXT
          );
        }
      }

      AccountDatabase.updateType(account.accountId, newAccount.type);

      // Return account with new type
      account.type = newAccount.type;
      return new Response(200, account, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }
}
