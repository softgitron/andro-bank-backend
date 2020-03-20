package com.server.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Scanner;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;

public class Controller {
  protected static final String SQL_ERROR =
    "An error has occurred during SQL operation.";
  protected static final SecureRandom random = new SecureRandom();
  protected static JsonWebSignature tokenGenerator;

  private static final String JWT_KEY_FILE = "./JWT_key.key";

  public static void initialize() {
    RsaJsonWebKey rsaKey;
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
    tokenGenerator = new JsonWebSignature();
    tokenGenerator.setKey(rsaKey.getPrivateKey());
    tokenGenerator.setKeyIdHeaderValue(rsaKey.getKeyId());
    tokenGenerator.setAlgorithmHeaderValue(
      AlgorithmIdentifiers.RSA_USING_SHA256
    );
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
}
