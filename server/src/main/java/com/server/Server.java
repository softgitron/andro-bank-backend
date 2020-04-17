package com.server;

import com.server.authentication.Authentication;
import com.server.controllers.FutureTransactionsController;
import com.server.database.DatabaseConnection;
import com.server.routes.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Server {
  private static final String DEFAULT_HOST = "localhost";
  private static final Integer DEFAULT_PORT = 8080;
  private static final Integer DEFAULT_BACK_LOGGING = 100;
  private static final Integer DEFAULT_THREAD_AMOUNT = 10;
  private static String HOST;
  private static Integer PORT;
  private static Integer BACK_LOGGING;
  private static Integer THREAD_AMOUNT;

  // Main funktion of the server that sets up all the functionalities.
  public static void main(String args[]) {
    // https://dzone.com/articles/simple-http-server-in-java
    HttpServer httpServer = null;
    loadParameters();
    try {
      httpServer =
        HttpServer.create(new InetSocketAddress(HOST, PORT), BACK_LOGGING);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Couldn't start server!");
      System.exit(1);
    }

    httpServer.createContext("/users", new UserRouter());
    httpServer.createContext("/accounts", new AccountRouter());
    httpServer.createContext("/cards", new CardRouter());
    httpServer.createContext("/transactions", new TransactionRouter());

    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
      THREAD_AMOUNT
    );

    threadPoolExecutor.setKeepAliveTime(1000, TimeUnit.SECONDS);
    threadPoolExecutor.allowCoreThreadTimeOut(true);
    httpServer.setExecutor(threadPoolExecutor);
    httpServer.start();
    DatabaseConnection.initialize();
    Authentication.initialize();

    // Initialize FutureTransaction executor
    // https://stackoverflow.com/questions/426758/running-a-java-thread-in-intervals
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    Runnable periodicTask = new Runnable() {

      public void run() {
        // Invoke method(s) to do the work
        FutureTransactionsController.checkFutureTransactions();
      }
    };
    executor.scheduleAtFixedRate(periodicTask, 0, 1, TimeUnit.MINUTES);

    System.out.println(String.format("Server started on port %d", PORT));
  }

  // Loads main environment variables related to basic server funktions
  private static void loadParameters() {
    HOST = (HOST = System.getenv("HOST")) != null ? HOST : DEFAULT_HOST;
    PORT = (PORT = getInt(System.getenv("PORT"))) != null ? PORT : DEFAULT_PORT;
    BACK_LOGGING =
      (BACK_LOGGING = getInt(System.getenv("BACK_LOGGING"))) != null
        ? BACK_LOGGING
        : DEFAULT_BACK_LOGGING;
    THREAD_AMOUNT =
      (THREAD_AMOUNT = getInt(System.getenv("THREAD_AMOUNT"))) != null
        ? THREAD_AMOUNT
        : DEFAULT_THREAD_AMOUNT;
  }

  private static Integer getInt(String string) {
    if (string == null) {
      return null;
    } else {
      return Integer.parseInt(string);
    }
  }
}
