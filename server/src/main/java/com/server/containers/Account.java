package com.server.containers;

public class Account {

  public enum AccountType {
    Savings,
    Credit,
    Normal
  }

  public Integer accountId;
  public String iban;
  public Integer balance;
  public AccountType type;
}
