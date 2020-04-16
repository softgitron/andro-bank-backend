package com.server.database;

import com.server.containers.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabase {

  // https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
  // Inser new user to the database
  // Returns new user id
  public static Integer insertUser(
    Integer bankId,
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
    statement.setInt(7, bankId);
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

  // Update user details based on the parameters
  // Returns nothing
  public static void updateUser(
    String username,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String hashedPassword
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "UPDATE Users SET userName = ?, firstName = ?, lastName = ?, email = ?, phoneNumber = ?, password = ?"
    );
    statement.setString(1, username);
    statement.setString(2, firstName);
    statement.setString(3, lastName);
    statement.setString(4, email);
    statement.setString(5, phoneNumber);
    statement.setString(6, hashedPassword);
    statement.executeUpdate();

    statement.close();
    connection.close();
    return;
  }

  // Retrieve information of the user based on the userId
  // Returns user details
  public static User retrieveUser(Integer userId) throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    // https://alvinalexander.com/java/edu/pj/jdbc/jdbc0003/
    PreparedStatement statement;
    statement =
      connection.prepareStatement(
        "SELECT userId, userName, firstName, lastName, email, phoneNumber, password, bankId FROM Users WHERE (userId = ?)"
      );
    statement.setInt(1, userId);
    User results = extractUserInformation(statement);
    connection.close();
    return results;
  }

  // Retrieve user details based on the email
  // Returns new user details
  public static User retrieveUser(String email) throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    // https://alvinalexander.com/java/edu/pj/jdbc/jdbc0003/
    PreparedStatement statement;
    statement =
      connection.prepareStatement(
        "SELECT userId, userName, firstName, lastName, email, phoneNumber, password, bankId FROM Users WHERE (email = ?)"
      );
    statement.setString(1, email);
    User results = extractUserInformation(statement);
    connection.close();
    return results;
  }

  // Helper function for pasring user details
  // Returns user details
  private static User extractUserInformation(PreparedStatement statement)
    throws SQLException {
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
    return user;
  }
}
