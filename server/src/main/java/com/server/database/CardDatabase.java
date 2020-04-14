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
    Integer accountId,
    Integer spendingLimit,
    Integer withdrawLimit,
    String area
  )
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "INSERT INTO Card (accountId, spending_limit, withdraw_limit, area) values (?, ?, ?, ?)",
      Statement.RETURN_GENERATED_KEYS
    );
    statement.setInt(1, accountId);
    statement.setInt(2, spendingLimit);
    statement.setInt(3, withdrawLimit);
    statement.setString(4, area);
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

  // Retrieve all cards for specific user account
  public static ArrayList<Card> retrieveCards(Integer accountId)
    throws SQLException {
    Connection connection = DatabaseConnection.getConnection();

    PreparedStatement statement = connection.prepareStatement(
      "SELECT cardId, withdraw_limit, spending_limit, area FROM Card WHERE accountId=?"
    );
    statement.setInt(1, accountId);
    ResultSet results = statement.executeQuery();
    ArrayList<Card> cards = new ArrayList<Card>();
    while (results.next() != false) {
      Card card = new Card();
      card.cardId = results.getInt(1);
      card.withdrawLimit = results.getInt(2);
      card.spendingLimit = results.getInt(3);
      card.area = results.getString(4);
      cards.add(card);
    }
    results.close();
    statement.close();
    connection.close();
    return cards;
  }
}
