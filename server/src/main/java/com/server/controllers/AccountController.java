package com.server.controllers;

import com.server.authentication.Token;
import com.server.containers.Bank;
import com.server.database.AccountDatabase;
import com.server.routes.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class AccountController extends Controller {

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
      NewAccount returnValues = new NewAccount();
      returnValues.accountId = accountId;
      returnValues.iban = iban;
      return new Response(201, returnValues, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }

  public static Response controllerRetrieveBanks() {
    try {
      ArrayList<Bank> banks = AccountDatabase.retrieveBanks();
      return new Response(201, banks, Response.ResponseType.JSON);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
  }
}

class NewAccount {
  public Integer accountId;
  public String iban;
}
