package com.server.database;

import com.server.containers.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class TransactionDatabase {

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
