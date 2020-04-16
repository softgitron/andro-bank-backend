package com.server.database;

import com.server.containers.Card;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CardDatabase {

  public static Integer insertCard(
    String cardNumber,
    Integer accountId,
    Integer spendingLimit,
    Integer withdrawLimit,
    String area
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "INSERT INTO Card (cardNumber, accountId, spendingLimit, withdrawLimit, area) values (?, ?, ?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    );
    statement.setString(1, cardNumber);
    statement.setInt(2, accountId);
    statement.setInt(3, spendingLimit);
    statement.setInt(4, withdrawLimit);
    statement.setString(5, area);
    statement.executeUpdate();
    ResultSet results = statement.getGeneratedKeys();
    if (!results.next()) {
      throw new SQLException("No generated id");
    }
    Integer cardId = results.getInt(1);
    results.close();
    statement.close();
    connection.close();
    return cardId;
  }

  // Retrieve all cards for specific user account by account id
  // Returns lis of cards
  public static ArrayList<Card> retrieveCardsByAccountId(Integer accountId)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT cardId, cardNumber, accountId, withdrawLimit, spendingLimit, area FROM Card WHERE accountId=?"
    );
    statement.setInt(1, accountId);
    ArrayList<Card> cards = getCards(statement);
    connection.close();
    return cards;
  }

  // Retrieve all cards for specific user account by card id
  // Returns lis of cards (should return only one)
  public static ArrayList<Card> retrieveCardsByCardId(Integer cardId)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT cardId, cardNumber, accountId, withdrawLimit, spendingLimit, area FROM Card WHERE cardId=?"
    );
    statement.setInt(1, cardId);
    ArrayList<Card> cards = getCards(statement);
    connection.close();
    return cards;
  }

  // Actual retrieval of the cards based on the prepared statements
  // described above
  // Returns list of cards
  private static ArrayList<Card> getCards(PreparedStatement statement)
    throws SQLException {
    ResultSet results = statement.executeQuery();
    ArrayList<Card> cards = new ArrayList<Card>();
    while (results.next() != false) {
      Card card = new Card();
      card.cardId = results.getInt(1);
      card.cardNumber = results.getString(2);
      card.accountId = results.getInt(3);
      card.withdrawLimit = results.getInt(4);
      card.spendingLimit = results.getInt(5);
      card.area = results.getString(6);
      cards.add(card);
    }
    results.close();
    statement.close();
    return cards;
  }

  // Update details of the cards like limits
  // Returns nothing
  public static void updateCard(
    Integer cardId,
    Integer withdrawLimit,
    Integer spendingLimit,
    String area
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "UPDATE Card SET withdrawLimit = ?, spendingLimit = ?, area = ? WHERE cardId = ?"
    );
    statement.setInt(1, withdrawLimit);
    statement.setInt(2, spendingLimit);
    statement.setString(3, area);
    statement.setInt(4, cardId);
    statement.executeUpdate();

    statement.close();
    connection.close();
  }
}
