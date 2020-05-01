package com.server.controllers;

import com.server.containers.Account;
import com.server.containers.FutureTransaction;
import com.server.containers.Transaction.TransactionType;
import com.server.database.AccountDatabase;
import com.server.database.TransactionDatabase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class FutureTransactionsController extends Controller {

  // Checks and executes any pending future transaction
  // Returns nothing
  public static void checkFutureTransactions() {
    ArrayList<FutureTransaction> futureTransactions = null;
    try {
      futureTransactions =
        TransactionDatabase.retrieveProcessableFutureTransactions();
    } catch (SQLException e) {
      System.out.println(
        "Unexpected error occurred during future transaction processing."
      );
      System.out.println("Please check sql server status imidiately.");
      System.exit(100);
    }
    for (FutureTransaction futureTransaction : futureTransactions) {
      try {
        Account fromAccount = getAccount(
          futureTransaction.futureTransferId,
          futureTransaction.fromAccountIban
        );

        // If account can't be found go to next statement
        if (fromAccount == null) {
          continue;
        }
        Account toAccount = getAccount(
          futureTransaction.futureTransferId,
          futureTransaction.toAccountIban
        );
        if (toAccount == null) {
          continue;
        }

        // Check that receiver is not same account
        if (fromAccount.iban.equals(toAccount.iban)) {
          TransactionDatabase.deleteFutureTransaction(
            futureTransaction.futureTransferId
          );
          return;
        }

        // Check that there is enough balance on the sender account
        if (
          !withdrawCanBeMade(
            fromAccount,
            futureTransaction.amount,
            TransactionType.Transfer
          )
        ) {
          TransactionDatabase.deleteFutureTransaction(
            futureTransaction.futureTransferId
          );
          continue;
        }
        futureTransaction = updateFutureTransfer(futureTransaction);

        // Execute transaction
        fromAccount.balance -= futureTransaction.amount;
        toAccount.balance += futureTransaction.amount;
        AccountDatabase.updateBalance(
          fromAccount.accountId,
          fromAccount.balance
        );
        AccountDatabase.updateBalance(toAccount.accountId, toAccount.balance);

        // Add to transactions
        TransactionDatabase.insertTransaction(
          futureTransaction.fromAccountId,
          futureTransaction.toAccountId,
          null,
          futureTransaction.amount,
          TransactionType.Transfer
        );
      } catch (SQLException e) {
        System.out.println(
          "Unexpected error occurred during future transaction processing."
        );
        System.out.println("Please check sql server status imidiately.");
        System.exit(101);
      }
    }
  }

  // Retrieves account based on the iban and removes future transaction if there is a miss match
  // Returns details of the account
  private static Account getAccount(
    Integer futureTransferId,
    String accountIban
  )
    throws SQLException {
    ArrayList<Account> accounts = AccountDatabase.retrieveAccounts(accountIban);
    if (accounts.size() != 1) {
      // There is something from with the accounts remove future transaction
      TransactionDatabase.deleteFutureTransaction(futureTransferId);
      return null;
    }

    return accounts.get(0);
  }

  // Updates status of the future transaction like how many more times transaction should be executed.
  // Returns updated future transaction
  private static FutureTransaction updateFutureTransfer(
    FutureTransaction futureTransaction
  )
    throws SQLException {
    // Update status of the future transfer
    if (
      futureTransaction.times == null || futureTransaction.atInterval == null
    ) {
      // If this was only postponed transfer delete it straight away.
      TransactionDatabase.deleteFutureTransaction(
        futureTransaction.futureTransferId
      );
    } else {
      // Remove one times
      futureTransaction.times--;
      if (futureTransaction.times == 0) {
        TransactionDatabase.deleteFutureTransaction(
          futureTransaction.futureTransferId
        );
      }

      // Calculate new time
      // https://www.baeldung.com/java-add-hours-date
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(futureTransaction.atTime);
      calendar.add(Calendar.MINUTE, futureTransaction.atInterval);
      futureTransaction.atTime = calendar.getTime();

      // Update future Transaction
      TransactionDatabase.updateFutureTransaction(
        futureTransaction.futureTransferId,
        futureTransaction.fromAccountId,
        futureTransaction.toAccountId,
        futureTransaction.amount,
        futureTransaction.atInterval,
        futureTransaction.times,
        futureTransaction.atTime
      );
    }
    return futureTransaction;
  }
}
