package com.server.database;

import com.server.containers.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class TransactionDatabase {

  public enum TransactionType {
    Transfer,
    DepWit,
    Payment
  }

  public static void insertTransaction(
    Integer fromId,
    Integer toId,
    Integer amount,
    TransactionType type
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "INSERT INTO MasterTransfer (fromId, toId, amount, time, type) values (?, ?, ?, ?, ?)"
    );
    if (fromId != null) {
      statement.setInt(1, fromId);
    } else {
      // https://stackoverflow.com/questions/18449708/how-to-insert-null-in-mysql-especially-int-datatype
      statement.setNull(1, Types.INTEGER);
    }
    statement.setInt(2, toId);
    statement.setInt(3, amount);

    // https://stackoverflow.com/questions/18614836/using-setdate-in-preparedstatement
    statement.setTimestamp(
      4,
      java.sql.Timestamp.valueOf(java.time.LocalDateTime.now())
    );
    statement.setString(5, type.name());
    statement.executeUpdate();

    statement.close();
    connection.close();
    return;
  }

  public static ArrayList<Transaction> retrieveTransactions(Integer accountId)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT fromId, toId, amount, time, type FROM MasterTransfer WHERE fromId = ? OR toId = ?"
    );
    statement.setInt(1, accountId);
    statement.setInt(2, accountId);
    ResultSet results = statement.executeQuery();
    ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    while (results.next() != false) {
      Transaction transaction = new Transaction();
      transaction.fromId = results.getInt(1);

      // https://stackoverflow.com/questions/2920364/checking-for-a-null-int-value-from-a-java-resultset
      if (results.wasNull()) {
        transaction.fromId = null;
      }
      transaction.toId = results.getInt(2);
      transaction.amount = results.getInt(3);
      transaction.time = results.getTimestamp(4);
      transaction.type = results.getString(5);
      transactions.add(transaction);
    }
    results.close();
    statement.close();
    connection.close();
    return transactions;
  }
}
