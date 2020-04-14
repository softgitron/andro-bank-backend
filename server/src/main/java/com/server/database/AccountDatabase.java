package com.server.database;

import com.server.containers.Account;
import com.server.containers.Bank;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
    if (!results.next()) {
      throw new SQLException("No generated id");
    }
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

  // Retrieve all accounts for specific user
  public static ArrayList<Account> retrieveAccounts(Integer userId)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();
    PreparedStatement statement = connection.prepareStatement(
      "SELECT accountId, iban, balance FROM Account WHERE userId=?"
    );
    statement.setInt(1, userId);
    ArrayList<Account> results = getAccounts(statement);
    connection.close();
    return results;
  }

  public static ArrayList<Account> retrieveAccounts(
    Integer userId,
    Integer accountId
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();
    PreparedStatement statement = connection.prepareStatement(
      "SELECT accountId, iban, balance FROM Account WHERE userId=? AND accountId=?"
    );
    statement.setInt(1, userId);
    statement.setInt(2, accountId);
    ArrayList<Account> results = getAccounts(statement);
    connection.close();
    return results;
  }

  private static ArrayList<Account> getAccounts(PreparedStatement statement)
    throws SQLException {
    ResultSet results = statement.executeQuery();
    ArrayList<Account> accounts = new ArrayList<Account>();
    while (results.next() != false) {
      Account account = new Account();
      account.accountId = results.getInt(1);
      account.iban = results.getString(2);
      account.balance = results.getInt(3);
      accounts.add(account);
    }
    results.close();
    statement.close();
    return accounts;
  }

  public static void updateBalance(
    Integer accountId,
    Integer balance,
    Integer userId
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "UPDATE Account SET balance = ? WHERE accountId = ? AND userId = ?"
    );
    statement.setInt(1, balance);
    statement.setInt(2, accountId);
    statement.setInt(3, userId);
    statement.executeUpdate();

    statement.close();
    connection.close();
  }
}
