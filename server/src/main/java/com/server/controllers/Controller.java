package com.server.controllers;

import com.server.containers.Account;
import com.server.containers.Account.AccountType;
import com.server.containers.Transaction;
import com.server.database.AccountDatabase;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Controller {
  protected static final String SQL_ERROR =
    "An error has occurred during SQL operation.";
  protected static final Integer MAX_CREADIT = -100000;
  protected static final SecureRandom random = new SecureRandom();

  protected static Account userOwnsAccount(Integer accountId, Integer userId) {
    ArrayList<Account> accounts;
    try {
      accounts = AccountDatabase.retrieveAccounts(userId);
    } catch (Exception e) {
      return null;
    }

    for (Account accountCandidate : accounts) {
      if (accountCandidate.accountId.equals(accountId)) {
        return accountCandidate;
      }
    }
    return null;
  }

  // Remember to check that user own account before executing this.
  protected static Boolean withdrawCanBeMade(
    Account account,
    Integer amount,
    Transaction.TransactionType type
  ) {
    return doWithdrawCheck(account, null, null, amount, type);
  }

  protected static Boolean withdrawCanBeMade(
    Account fromAccount,
    Integer toAccountId,
    Integer userId,
    Integer amount,
    Transaction.TransactionType type
  ) {
    return doWithdrawCheck(fromAccount, toAccountId, userId, amount, type);
  }

  private static Boolean doWithdrawCheck(
    Account fromAccount,
    Integer toAccountId,
    Integer userId,
    Integer amount,
    Transaction.TransactionType type
  ) {
    // Check that there is enough money
    switch (fromAccount.type) {
      case Credit:
        if (fromAccount.balance - amount < MAX_CREADIT) {
          return false;
        }
        break;
      default:
        if (fromAccount.balance - amount < 0) {
          return false;
        }
        break;
    }

    // Check that account type can be used for action
    switch (type) {
      case Transfer:
        if (fromAccount.type == AccountType.Savings) {
          return userOwnsDestination(fromAccount, toAccountId, userId);
        }
        break;
      case Payment:
        if (fromAccount.type == AccountType.Savings) {
          return false;
        }
        break;
      default:
        return true;
    }
    return true;
  }

  private static Boolean userOwnsDestination(
    Account fromAccount,
    Integer toAccountId,
    Integer userId
  ) {
    if (toAccountId == null) {
      return false;
    }
    return userOwnsAccount(toAccountId, userId) != null;
  }
}
