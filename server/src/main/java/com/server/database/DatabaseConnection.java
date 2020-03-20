package com.server.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import org.apache.commons.dbcp2.BasicDataSource;

public class DatabaseConnection {
  private static final String DEFAULT_DATABASE_URL =
    "jdbc:mysql://localhost:3306/olio1";
  private static final String DEFAULT_DATABASE_USERNAME = "olio1admin";
  private static final String DEFAULT_DATABASE_PASSWORD = "YOUR PASSWORD HERE";

  private DatabaseConnection instance = new DatabaseConnection();

  private DatabaseConnection() {}

  public DatabaseConnection getInstance() {
    return instance;
  }

  private static BasicDataSource dataSource;

  public static void initialize() {
    // https://www.technicalkeeda.com/java-tutorials/read-environment-variables-in-java
    // https://stackoverflow.com/questions/29537639/check-if-returned-value-is-not-null-and-if-so-assign-it-in-one-line-with-one-m
    String databaseUrl = (databaseUrl = System.getenv("DATABASE_URL")) != null
      ? databaseUrl
      : DEFAULT_DATABASE_URL;
    String databaseUsername = (
        databaseUsername = System.getenv("MYSQL_USER")
      ) !=
      null
      ? databaseUsername
      : DEFAULT_DATABASE_USERNAME;
    String databasePassword = (
        databasePassword = System.getenv("MYSQL_PASSWORD")
      ) !=
      null
      ? databasePassword
      : DEFAULT_DATABASE_PASSWORD;

    // If password is in password file retrieve it
    if (databasePassword.startsWith("/")) {
      try {
        File passwordFile = new File(databasePassword);
        Scanner fileReader = new Scanner(passwordFile);
        databasePassword = fileReader.nextLine();
        fileReader.close();
      } catch (IOException e) {
        System.out.println("Unable to retrieve password from a file.");
        System.exit(5);
      }
    }

    // https://www.baeldung.com/java-connection-pooling
    dataSource = new BasicDataSource();
    dataSource.setUrl(databaseUrl);
    dataSource.setUsername(databaseUsername);
    dataSource.setPassword(databasePassword);
    dataSource.setMinIdle(2);
    dataSource.setMinIdle(5);
    dataSource.setMaxOpenPreparedStatements(20);
  }

  public static Connection getConnection() {
    for (int i = 10; i > 0; i--) {
      try {
        return dataSource.getConnection();
      } catch (SQLException e) {
        try {
          System.out.println(
            String.format(
              "Problems with a database connection.\nTrying again in 1 second.\n%d trials left.",
              i
            )
          );
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          System.exit(3);
        }
      }
    }
    System.exit(2);
    return null;
  }
}
