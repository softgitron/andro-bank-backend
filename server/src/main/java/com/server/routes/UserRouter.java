package com.server.routes;

import com.server.controllers.UserController;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class UserRouter extends Router {
  private final String USERNAME_REGEX = "^[A-Za-z0-9\\._]{3,}$";
  private final String NAME_REGEX = "^[A-Za-z]{2,}$";
  private final String EMAIL_REGEX =
    "^[A-Za-z0-9_.]{2,}@[A-Za-z0-9_]{2,}\\.[A-Za-z]{2,3}$";
  private final String PHONE_NUMBER_REGEX = "^\\+?[0-9]{6,12}$";
  private final String PASSWORD_REGEX = ".{5,}";

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    this.httpExchange = httpExchange;
    String methodAddress =
      httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI();
    switch (methodAddress) {
      case "POST /users/createUser":
        routeCreateUser();
        break;
    }
  }

  private void routeCreateUser() {
    NewUser newUser = (NewUser) decodeJson(NewUser.class);

    // Validate provided information with regexes
    // Might not be definitive solution but better than nothing.
    if (
      newUser.username.matches(USERNAME_REGEX) &&
      newUser.firstName.matches(NAME_REGEX) &&
      newUser.lastName.matches(NAME_REGEX) &&
      newUser.email.matches(EMAIL_REGEX) &&
      newUser.phoneNumber.matches(PHONE_NUMBER_REGEX) &&
      newUser.password.matches(PASSWORD_REGEX)
    ) {
      // Go to controller and handle request
      Response response = UserController.controllerCreateUser(
        newUser.username,
        newUser.firstName,
        newUser.lastName,
        newUser.email,
        newUser.phoneNumber,
        newUser.password
      );
      sendResponse(response);
    } else {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
    }
  }
}

class NewUser {
  public String username;
  public String firstName;
  public String lastName;
  public String email;
  public String phoneNumber;
  public String password;
}
