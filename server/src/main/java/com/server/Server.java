package com.server;

import com.server.controllers.Controller;
import com.server.database.DatabaseConnection;
import com.server.routes.*;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class Server {
  private static final String HOST = "localhost";
  private static final Integer PORT = 8080;
  private static final Integer BACK_LOGGING = 100;

  public static void main(String args[]) {
    // https://dzone.com/articles/simple-http-server-in-java
    HttpServer httpServer = null;
    try {
      httpServer =
        HttpServer.create(new InetSocketAddress(HOST, PORT), BACK_LOGGING);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Couldn't start server!");
      System.exit(1);
    }

    httpServer.createContext("/users", new UserRouter());

    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
      10
    );
    httpServer.setExecutor(threadPoolExecutor);
    httpServer.start();
    DatabaseConnection.initialize();
    Controller.initialize();
    System.out.println(String.format("Server started on port %d", PORT));
  }
}
