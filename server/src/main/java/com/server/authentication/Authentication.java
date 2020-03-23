package com.server.authentication;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

// https://bitbucket.org/b_c/jose4j/wiki/Home
public class Authentication {
  private static final Integer DEFAULT_EXPIRATION_TIME = 10000;

  private static RsaJsonWebKey rsaKey;

  private static final String JWT_KEY_FILE = "./JWT_key.key";

  public static void initialize() {
    try {
      File rsaFile = new File(JWT_KEY_FILE);
      Scanner fileReader = new Scanner(rsaFile);
      String rsaString = "";
      while (fileReader.hasNext()) {
        rsaString += fileReader.nextLine();
      }
      fileReader.close();
      PublicJsonWebKey rsaKeyRaw = PublicJsonWebKey.Factory.newPublicJwk(
        rsaString
      );
      rsaKey = (RsaJsonWebKey) rsaKeyRaw;
    } catch (IOException | JoseException e) {
      rsaKey = generateNewKey();
    }
    rsaKey.setKeyId("andro-bank-key");
  }

  private static RsaJsonWebKey generateNewKey() {
    RsaJsonWebKey rsaJsonWebKey = null;
    try {
      rsaJsonWebKey = RsaJwkGenerator.generateJwk(4096);
      String rsaString = rsaJsonWebKey.toJson(
        RsaJsonWebKey.OutputControlLevel.INCLUDE_PRIVATE
      );
      FileWriter fileWriter = new FileWriter(JWT_KEY_FILE);
      fileWriter.write(rsaString);
      fileWriter.close();
    } catch (IOException | JoseException e) {
      System.out.println("Can't generate JWT key!");
      e.printStackTrace();
      System.exit(6);
    }
    return rsaJsonWebKey;
  }

  private static JsonWebSignature newTokenHandler() {
    JsonWebSignature tokenHandler;
    tokenHandler = new JsonWebSignature();
    tokenHandler.setKey(rsaKey.getPrivateKey());
    tokenHandler.setKeyIdHeaderValue(rsaKey.getKeyId());
    tokenHandler.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
    return tokenHandler;
  }

  private static JwtClaims createClaims(Token token) {
    Gson gson = new Gson();
    String payload = gson.toJson(token, Token.class);
    JwtClaims claims = new JwtClaims();
    claims.setIssuer("andro-bank-backend");
    claims.setAudience("andro-bank");
    claims.setExpirationTimeMinutesInTheFuture(DEFAULT_EXPIRATION_TIME);
    claims.setGeneratedJwtId();
    claims.setIssuedAtToNow();
    claims.setNotBeforeMinutesInThePast(10);
    claims.setSubject("Authentication");
    claims.setClaim("payload", payload);
    return claims;
  }

  public static String createJWT(Integer userId) {
    JsonWebSignature tokenGenerator = newTokenHandler();
    Token token = new Token(userId);
    JwtClaims claims = createClaims(token);
    tokenGenerator.setPayload(claims.toJson());
    String jwt = null;
    try {
      jwt = tokenGenerator.getCompactSerialization();
    } catch (JoseException e) {
      System.out.println("Unable to generate JWT:s.");
      e.printStackTrace();
    }
    return jwt;
  }

  private static JwtConsumer getConsumer() {
    JwtConsumer consumer = new JwtConsumerBuilder()
      .setRequireExpirationTime() // the JWT must have an expiration time
      .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
      .setRequireSubject() // the JWT must have a subject claim
      .setExpectedIssuer("andro-bank-backend") // whom the JWT needs to have been issued by
      .setExpectedAudience("andro-bank") // to whom the JWT is intended for
      .setVerificationKey(rsaKey.getKey()) // verify the signature with the public key
      .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
        ConstraintType.WHITELIST,
        AlgorithmIdentifiers.RSA_USING_SHA256
      ) // which is only RS256 here
      .build(); // create the JwtConsumer instance
    return consumer;
  }

  public static Token validateJWT(String rawToken) {
    try {
      JwtConsumer consumer = getConsumer();
      JwtClaims jwtClaims = consumer.processToClaims(rawToken);
      String rawPayload = (String) (jwtClaims.getClaimValue("payload"));
      Gson gson = new Gson();
      Token token = gson.fromJson(rawPayload, Token.class);
      token.setIsValid(true);
      return token;
    } catch (InvalidJwtException e) {
      Token token = new Token(0);
      return token.setIsValid(false);
    }
  }
}
