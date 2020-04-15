package com.server.containers;

import java.util.Date;

public class Transaction {

  public enum TransactionType {
    Transfer,
    Deposit,
    Withdraw,
    Payment
  }

  public Integer transferId;
  public Integer fromAccountId;
  public String fromAccountIban;
  public String fromAccountBic;
  public Integer toAccountId;
  public String toAccountIban;
  public String toAccountBic;
  public Integer cardId;
  public String cardNumber;
  public Integer amount;
  public Date time;
  public TransactionType type;
}
