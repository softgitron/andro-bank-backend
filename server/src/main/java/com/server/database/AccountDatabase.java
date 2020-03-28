package com.server.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.server.containers.Bank;

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

  // Retrieve all banks from database
  public static ArrayList<Bank> retrieveBanks() throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT bankId, name, bic FROM Bank"
    );
    ResultSet results = statement.executeQuery();
    ArrayList<Bank> banks = new ArrayList<Bank>();
    while (results.next() != false) {
      Bank onebank = new Bank();
      onebank.bankId = results.getInt(1);
      onebank.name = results.getString(2);
      onebank.bic = results.getString(3);
      banks.add(onebank);
    }
    results.close();
    statement.close();
    connection.close();
    return banks;

  }
}
