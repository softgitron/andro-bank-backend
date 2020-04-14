package com.server.controllers;

import com.server.containers.Account;
import com.server.database.AccountDatabase;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Controller {
  protected static final String SQL_ERROR =
    "An error has occurred during SQL operation.";
  protected static final SecureRandom random = new SecureRandom();

  protected static Boolean userOwnsAccount(Integer accountId, Integer userId) {
    ArrayList<Account> accounts;
    try {
      accounts = AccountDatabase.retrieveAccounts(userId);
    } catch (Exception e) {
      return false;
    }

    for (Account accountCandidate : accounts) {
      if (accountCandidate.accountId.equals(accountId)) {
        return true;
      }
    }
    return false;
  }
}
