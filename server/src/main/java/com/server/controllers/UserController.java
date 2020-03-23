package com.server.controllers;

import com.server.authentication.Authentication;
import com.server.containers.User;
import com.server.database.UserDatabase;
import com.server.routes.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.jose4j.base64url.Base64;

public class UserController extends Controller {
  private static final String loginError =
    "User does not exist or password is wrong.";

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
    byte[] hash = hashPassword(password, salt);
    String hashedPassword = Base64.encode(hash) + "$" + Base64.encode(salt);
    String token;

    try {
      Integer userId = UserDatabase.insertUser(
        username,
        firstName,
        lastName,
        email,
        phoneNumber,
        hashedPassword
      );

      token = Authentication.createJWT(userId);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
    return new Response(201, "OK", Response.ResponseType.TEXT, token);
  }

  public static Response controllerLogin(String email, String password) {
    try {
      User user = UserDatabase.retrieveUser(email);
      String[] information = user.password.split("\\$", 2);
      byte[] hash = Base64.decode(information[0]);
      byte[] salt = Base64.decode(information[1]);
      byte[] hashToCompare = hashPassword(password, salt);

      // https://www.tutorialspoint.com/java/util/arrays_equals_byte.htm
      if (Arrays.equals(hash, hashToCompare)) {
        // Passwords match
        String token = Authentication.createJWT(user.userId);
        user.password = null;
        return new Response(201, user, Response.ResponseType.JSON, token);
      } else {
        return new Response(401, loginError, Response.ResponseType.TEXT);
      }
    } catch (SQLException e) {
      return new Response(401, loginError, Response.ResponseType.TEXT);
    }
  }

  private static byte[] hashPassword(String password, byte[] salt) {
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
    return hash;
  }
}
