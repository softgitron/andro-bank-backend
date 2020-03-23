package com.server.authentication;

public class Token {
  // public static final String TIMESTAMP_FORMAT = "dd/MM/yyyy HH:mm:ss";
  private Boolean isValid;
  public Integer userId;

  // public Integer expirationTimeInMinutes;
  // public String timestamp;
  public Token(Integer userId) {
    this.userId = userId;
  // this.expirationTimeInMinutes = expirationTimeInMinutes;
  // https://www.javatpoint.com/java-get-current-date
  // SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
  // Date date = new Date();
  // this.timestamp = formatter.format(date);
  }

  public Token setIsValid(Boolean isValid) {
    this.isValid = isValid;
    return this;
  }

  public Boolean getIsValid() {
    return isValid;
  }
}
