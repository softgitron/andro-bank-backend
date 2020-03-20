package com.server.authentication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Token {
  public static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm:ss";

  public Long userId;
  public Integer expirationTimeInMinutes;
  public String timestamp;

  public Token(Long userId, Integer expirationTimeInMinutes) {
    this.userId = userId;
    this.expirationTimeInMinutes = expirationTimeInMinutes;

    // https://www.javatpoint.com/java-get-current-date
    SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
    Date date = new Date();
    this.timestamp = formatter.format(date);
  }
}
