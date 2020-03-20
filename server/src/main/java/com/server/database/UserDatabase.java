package com.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabase {

  // https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
  public static ResultSet insertUser(
    String username,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String hashedPassword
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    // https://stackoverflow.com/questions/2120255/resultset-exception-before-start-of-result-set
    // https://stackoverflow.com/questions/1915166/how-to-get-the-insert-id-in-jdbc
    PreparedStatement statement = connection.prepareStatement(
      "INSERT INTO Users (userName, firstName, lastName, email, phoneNumber, password, bankId) values (?, ?, ?, ?, ?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    );
    statement.setString(1, username);
    statement.setString(2, firstName);
    statement.setString(3, lastName);
    statement.setString(4, email);
    statement.setString(5, phoneNumber);
    statement.setString(6, hashedPassword);
    statement.setInt(7, 0);
    statement.executeUpdate();
    return statement.getGeneratedKeys();
  }
}
