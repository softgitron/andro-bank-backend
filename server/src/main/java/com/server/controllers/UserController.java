package com.server.controllers;

import com.server.authentication.Authentication;
import com.server.authentication.Token;
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

  // Creates new user based on the parameters
  // Returns details of the new user and authentication token
  public static Response controllerCreateUser(User user) {
    // https://www.baeldung.com/java-password-hashing
    // Hashing password
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    byte[] hash = hashPassword(user.password, salt);
    String hashedPassword = Base64.encode(hash) + "$" + Base64.encode(salt);
    String token;

    try {
      Integer userId = UserDatabase.insertUser(
        user.bankId,
        user.username,
        user.firstName,
        user.lastName,
        user.email,
        user.phoneNumber,
        hashedPassword
      );

      token = Authentication.createJWT(userId);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }
    user.password = null;
    return new Response(201, user, Response.ResponseType.JSON, token);
  }

  // Updates details of the user
  // Returns details of the updated user
  public static Response controllerUpdateUserDetails(
    User newUserDetails,
    Token authorization
  ) {
    // Retrieve old user details for combining details
    User oldUserDetails;
    try {
      oldUserDetails = UserDatabase.retrieveUser(authorization.userId);
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }

    // Combine new and old information
    User preparedDetails = new User();
    preparedDetails.bankId = oldUserDetails.bankId;
    preparedDetails.username =
      (newUserDetails.username != null)
        ? newUserDetails.username
        : oldUserDetails.username;
    preparedDetails.firstName =
      (newUserDetails.firstName != null)
        ? newUserDetails.firstName
        : oldUserDetails.firstName;
    preparedDetails.lastName =
      (newUserDetails.lastName != null)
        ? newUserDetails.lastName
        : oldUserDetails.lastName;
    preparedDetails.email =
      (newUserDetails.email != null)
        ? newUserDetails.email
        : oldUserDetails.email;
    preparedDetails.phoneNumber =
      (newUserDetails.phoneNumber != null)
        ? newUserDetails.phoneNumber
        : oldUserDetails.phoneNumber;

    if (newUserDetails.password != null) {
      byte[] salt = new byte[16];
      random.nextBytes(salt);
      byte[] hash = hashPassword(newUserDetails.password, salt);
      preparedDetails.password =
        Base64.encode(hash) + "$" + Base64.encode(salt);
    } else {
      preparedDetails.password = oldUserDetails.password;
    }

    try {
      UserDatabase.updateUser(
        authorization.userId,
        preparedDetails.username,
        preparedDetails.firstName,
        preparedDetails.lastName,
        preparedDetails.email,
        preparedDetails.phoneNumber,
        preparedDetails.password
      );
    } catch (SQLException e) {
      return new Response(500, SQL_ERROR, Response.ResponseType.TEXT);
    }

    // Remove password and bank id from return values
    preparedDetails.password = null;
    preparedDetails.bankId = null;
    return new Response(200, preparedDetails, Response.ResponseType.JSON);
  }

  // Checks email and password for login
  // Returns new authentication token
  public static Response controllerLogin(User loginUser) {
    try {
      User user = UserDatabase.retrieveUser(loginUser.email);

      // Check that user has correct bank
      if (user.bankId != loginUser.bankId) {
        return new Response(401, loginError, Response.ResponseType.TEXT);
      }
      String[] information = user.password.split("\\$", 2);
      byte[] hash = Base64.decode(information[0]);
      byte[] salt = Base64.decode(information[1]);
      byte[] hashToCompare = hashPassword(loginUser.password, salt);

      // https://www.tutorialspoint.com/java/util/arrays_equals_byte.htm
      if (Arrays.equals(hash, hashToCompare)) {
        // Passwords match
        String token = Authentication.createJWT(user.userId);
        user.password = null;
        return new Response(200, user, Response.ResponseType.JSON, token);
      } else {
        return new Response(401, loginError, Response.ResponseType.TEXT);
      }
    } catch (SQLException e) {
      return new Response(401, loginError, Response.ResponseType.TEXT);
    }
  }

  // Hashes password with PBKDF2 and salts it
  // Returns hashed password
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
