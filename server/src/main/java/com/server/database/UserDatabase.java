package com.server.database;

import com.server.containers.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabase {

  // https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
  public static Integer insertUser(
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

    // https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
    ResultSet results = statement.getGeneratedKeys();
    results.next();
    Integer userId = results.getInt(1);
    results.close();
    statement.close();

    // Connection must be closed otherwice there will be big problems with threading
    connection.close();
    return userId;
  }

  public static User retrieveUser(String email) throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    // https://alvinalexander.com/java/edu/pj/jdbc/jdbc0003/
    PreparedStatement statement = connection.prepareStatement(
      "SELECT userId, userName, firstName, lastName, email, phoneNumber, password, bankId FROM Users WHERE (email = ?)"
    );
    statement.setString(1, email);
    ResultSet results = statement.executeQuery();
    results.next();
    User user = new User();
    user.userId = results.getInt(1);
    user.username = results.getString(2);
    user.firstName = results.getString(3);
    user.lastName = results.getString(4);
    user.email = results.getString(5);
    user.phoneNumber = results.getString(6);
    user.password = results.getString(7);
    user.bankId = results.getInt(8);
    results.close();
    statement.close();
    connection.close();
    return user;
  }
}
