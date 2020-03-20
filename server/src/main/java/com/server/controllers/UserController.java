package com.server.controllers;

import com.google.gson.Gson;
import com.server.authentication.Token;
import com.server.database.UserDatabase;
import com.server.routes.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.jose4j.lang.JoseException;

public class UserController extends Controller {
  private static final Integer DEFAULT_EXPIRATION_TIME = 10000;

  public static Response controllerCreateUser(
    String username,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String password
  ) {
    // https://www.baeldung.com/java-password-hashing
    // Hashing password
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
    byte[] hash = null;
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance(
        "PBKDF2WithHmacSHA1"
      );
      hash = factory.generateSecret(spec).getEncoded();
    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
      System.out.println(
        "Unable to initialize password hashing. Check your java installation."
      );
      System.exit(4);
    }
    String hashedPassword = hash.toString();
    String token;
    try {
      ResultSet results = UserDatabase.insertUser(
        username,
        firstName,
        lastName,
        email,
        phoneNumber,
        hashedPassword
      );

      // https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
      results.next();
      Long userId = results.getLong(1);
      token = createJWT(userId);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
    return new Response(201, "OK", Response.ResponseType.TEXT, token);
  }

  private static String createJWT(Long userId) {
    Token token = new Token(userId, DEFAULT_EXPIRATION_TIME);
    Gson gson = new Gson();
    String json = gson.toJson(token);
    tokenGenerator.setPayload(json);
    String jwt = null;
    try {
      jwt = tokenGenerator.getCompactSerialization();
    } catch (JoseException e) {
      System.out.println("Unable to generate JWT:s.");
      e.printStackTrace();
    }
    return jwt;
  }
}
