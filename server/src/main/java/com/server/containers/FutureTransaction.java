package com.server.containers;

import java.util.Date;

public class FutureTransaction extends Transaction {
  public Integer futureTransferId;
  public Integer atInterval; // How often transfer is processed in minutes
  public Integer times;
  public Date atTime;
}
