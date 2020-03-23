package com.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountDatabase {

  // https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
  public static Integer insertAccount(Integer userId, String iban)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "INSERT INTO Account (userId, iban, balance) values (?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    );
    statement.setInt(1, userId);
    statement.setString(2, iban);
    statement.setInt(3, 0);
    statement.executeUpdate();
    ResultSet results = statement.getGeneratedKeys();
    results.next();
    Integer accountId = results.getInt(1);
    results.close();
    statement.close();
    connection.close();
    return accountId;
  }
}
