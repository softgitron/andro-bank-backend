package com.server.database;

import com.server.containers.FutureTransaction;
import com.server.containers.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

public class TransactionDatabase {

  // Insers new transaction based on the details
  // Return nothing
  public static void insertTransaction(
    Integer fromAccountId,
    Integer toAccountId,
    Integer cardId,
    Integer amount,
    Transaction.TransactionType type
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "INSERT INTO MasterTransfer (fromAccountId, toAccountId, cardId, amount, time, type) values (?, ?, ?, ?, ?, ?)"
    );

    setNullInt(statement, 1, fromAccountId);
    setNullInt(statement, 2, toAccountId);
    setNullInt(statement, 3, cardId);
    statement.setInt(4, amount);

    // https://stackoverflow.com/questions/18614836/using-setdate-in-preparedstatement
    statement.setTimestamp(
      5,
      java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())
    );
    statement.setString(6, type.name());
    statement.executeUpdate();

    statement.close();
    connection.close();
    return;
  }

  // Inserts new future transaction to the database based on the parameters
  // Returns nothing
  public static void insertFutureTransaction(
    Integer fromAccountId,
    Integer toAccountId,
    Integer amount,
    Integer atInterval,
    Integer times,
    Date atTime
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "INSERT INTO FutureTransfer (fromAccountId, toAccountId, amount, atInterval, times, atTime) values (?, ?, ?, ?, ?, ?)"
    );

    setNullInt(statement, 1, fromAccountId);
    setNullInt(statement, 2, toAccountId);
    setNullInt(statement, 3, amount);
    setNullInt(statement, 4, atInterval);
    setNullInt(statement, 5, times);
    statement.setTimestamp(6, new java.sql.Timestamp(atTime.getTime()));

    statement.executeUpdate();

    statement.close();
    connection.close();
    return;
  }

  // Helper function to set actual null to database instead
  // of zero if the original value is null
  // Return none
  private static void setNullInt(
    PreparedStatement statement,
    Integer index,
    Integer value
  )
    throws SQLException {
    if (value != null) {
      statement.setInt(index, value);
    } else {
      // https://stackoverflow.com/questions/18449708/how-to-insert-null-in-mysql-especially-int-datatype
      statement.setNull(index, Types.INTEGER);
    }
  }

  // Update status of the future transaction.
  // This is used by futureTransactions controller
  // Return none
  public static void updateFutureTransaction(
    Integer futureTransferId,
    Integer fromAccountId,
    Integer toAccountId,
    Integer amount,
    Integer atInterval,
    Integer times,
    Date atTime
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "UPDATE FutureTransfer SET fromAccountId = ?, toAccountId = ?, amount = ?, atInterval = ?, times = ?, atTime = ? WHERE futureTransferId = ?"
    );

    setNullInt(statement, 1, fromAccountId);
    setNullInt(statement, 2, toAccountId);
    setNullInt(statement, 3, amount);
    setNullInt(statement, 4, atInterval);
    setNullInt(statement, 5, times);
    statement.setTimestamp(6, new java.sql.Timestamp(atTime.getTime()));
    statement.setInt(7, futureTransferId);

    statement.executeUpdate();

    statement.close();
    connection.close();
    return;
  }

  // Retrieves transactions based on the account from the database
  // Returns list of transactions
  public static ArrayList<Transaction> retrieveTransactions(Integer accountId)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT fromAccountId, fromAccount.iban, fromBank.bic, toAccountId, toAccount.iban, " +
      "toBank.bic, MasterTransfer.cardId, Card.cardNumber, amount, time, MasterTransfer.type FROM MasterTransfer " +
      "LEFT JOIN Account AS fromAccount ON fromAccount.accountId = fromAccountId " +
      "LEFT JOIN Account AS toAccount ON toAccount.accountId = toAccountId " +
      "LEFT JOIN Users AS fromUser ON fromUser.userId = fromAccount.userId " +
      "LEFT JOIN Users AS toUser ON toUser.userId = toAccount.userId " +
      "LEFT JOIN Bank AS fromBank ON fromBank.bankId = fromUser.bankId " +
      "LEFT JOIN Bank AS toBank ON toBank.bankId = toUser.bankId " +
      "LEFT JOIN Card ON Card.cardId = MasterTransfer.cardId " +
      "WHERE fromAccountId = ? OR toAccountId = ? ORDER BY MasterTransfer.time, transferId"
    );
    statement.setInt(1, accountId);
    statement.setInt(2, accountId);
    ResultSet results = statement.executeQuery();
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    while (results.next() != false) {
      Transaction transaction = new Transaction();
      transaction.fromAccountId = getNullInt(results, 1);
      transaction.fromAccountIban = getNullString(results, 2);
      transaction.fromAccountBic = getNullString(results, 3);
      transaction.toAccountId = getNullInt(results, 4);
      transaction.toAccountIban = getNullString(results, 5);
      transaction.toAccountBic = getNullString(results, 6);
      transaction.cardId = getNullInt(results, 7);
      transaction.cardNumber = getNullString(results, 8);
      transaction.amount = getNullInt(results, 9);
      transaction.time = results.getTimestamp(10);

      // https://www.baeldung.com/java-string-to-enum
      transaction.type =
        Transaction.TransactionType.valueOf(results.getString(11));
      transactions.add(transaction);
    }
    results.close();
    statement.close();
    connection.close();
    return transactions;
  }

  // Retrieves future transactions based on the account from the database based on the accountId
  // Returns list of future transactions
  public static ArrayList<FutureTransaction> retrieveFutureTransactions(
    Integer accountId
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT futureTransferId, fromAccountId, fromAccount.iban, fromBank.bic, toAccountId, toAccount.iban, " +
      "toBank.bic, amount, atInterval, times, atTime FROM FutureTransfer " +
      "LEFT JOIN Account AS fromAccount ON fromAccount.accountId = fromAccountId " +
      "LEFT JOIN Account AS toAccount ON toAccount.accountId = toAccountId " +
      "LEFT JOIN Users AS fromUser ON fromUser.userId = fromAccount.userId " +
      "LEFT JOIN Users AS toUser ON toUser.userId = toAccount.userId " +
      "LEFT JOIN Bank AS fromBank ON fromBank.bankId = fromUser.bankId " +
      "LEFT JOIN Bank AS toBank ON toBank.bankId = toUser.bankId " +
      "WHERE fromAccountId = ? ORDER BY FutureTransfer.atTime, futureTransferId"
    );
    statement.setInt(1, accountId);
    ArrayList<FutureTransaction> transactions = getFutureTransactions(
      statement
    );
    connection.close();
    return transactions;
  }

  // Retrieves all future transaction for FutureTransactionController
  // Returns list of future transactions
  public static ArrayList<FutureTransaction> retrieveProcessableFutureTransactions()
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT futureTransferId, fromAccountId, fromAccount.iban, fromBank.bic, toAccountId, toAccount.iban, " +
      "toBank.bic, amount, atInterval, times, atTime FROM FutureTransfer " +
      "LEFT JOIN Account AS fromAccount ON fromAccount.accountId = fromAccountId " +
      "LEFT JOIN Account AS toAccount ON toAccount.accountId = toAccountId " +
      "LEFT JOIN Users AS fromUser ON fromUser.userId = fromAccount.userId " +
      "LEFT JOIN Users AS toUser ON toUser.userId = toAccount.userId " +
      "LEFT JOIN Bank AS fromBank ON fromBank.bankId = fromUser.bankId " +
      "LEFT JOIN Bank AS toBank ON toBank.bankId = toUser.bankId " +
      "WHERE atTime < NOW()"
    );
    ArrayList<FutureTransaction> transactions = getFutureTransactions(
      statement
    );
    connection.close();
    return transactions;
  }

  // Do actual database search for the future transactions
  // Returns list of future transactions
  private static ArrayList<FutureTransaction> getFutureTransactions(
    PreparedStatement statement
  )
    throws SQLException {
    ResultSet results = statement.executeQuery();
    ArrayList<FutureTransaction> transactions = new ArrayList<FutureTransaction>();
    while (results.next() != false) {
      FutureTransaction transaction = new FutureTransaction();
      transaction.futureTransferId = getNullInt(results, 1);
      transaction.fromAccountId = getNullInt(results, 2);
      transaction.fromAccountIban = getNullString(results, 3);
      transaction.fromAccountBic = getNullString(results, 4);
      transaction.toAccountId = getNullInt(results, 5);
      transaction.toAccountIban = getNullString(results, 6);
      transaction.toAccountBic = getNullString(results, 7);
      transaction.amount = getNullInt(results, 8);
      transaction.atInterval = getNullInt(results, 9);
      transaction.times = getNullInt(results, 10);
      transaction.atTime = results.getTimestamp(11);
      transactions.add(transaction);
    }
    results.close();
    statement.close();
    return transactions;
  }

  public static void deleteFutureTransaction(Integer futureTransferId)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "DELETE FROM FutureTransfer WHERE futureTransferId = ?"
    );

    statement.setInt(1, futureTransferId);
    statement.executeUpdate();

    statement.close();
    connection.close();
    return;
  }

  // Helper functions for retrieving real nulls if
  // there is null in the database
  // https://stackoverflow.com/questions/2920364/checking-for-a-null-int-value-from-a-java-resultset
  private static Integer getNullInt(ResultSet results, Integer index)
    throws SQLException {
    Integer value = results.getInt(index);
    if (results.wasNull()) {
      value = null;
    }
    return value;
  }

  private static String getNullString(ResultSet results, Integer index)
    throws SQLException {
    String value = results.getString(index);
    if (results.wasNull()) {
      value = null;
    }
    return value;
  }
}
