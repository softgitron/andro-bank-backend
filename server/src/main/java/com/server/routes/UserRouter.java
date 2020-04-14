package com.server.routes;

import com.server.containers.User;
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
    super.handle(httpExchange);
    switch (methodAddress) {
      case "POST /users/createUser":
        routeCreateUser();
        break;
      case "POST /users/updateUserDetails":
        routeUpdateUserDetails();
        break;
      case "POST /users/login":
        routeLogin();
        break;
      default:
        sendResponse(400, BAD_PATH, Response.ResponseType.TEXT);
        break;
    }
  }

  private void routeCreateUser() {
    User newUser;
    try {
      newUser = (User) decodeJson(User.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    // Validate provided information with regexes
    // Might not be definitive solution but better than nothing.
    if (
      newUser.username != null &&
      newUser.firstName != null &&
      newUser.lastName != null &&
      newUser.email != null &&
      newUser.phoneNumber != null &&
      newUser.password != null &&
      newUser.username.matches(USERNAME_REGEX) &&
      newUser.firstName.matches(NAME_REGEX) &&
      newUser.lastName.matches(NAME_REGEX) &&
      newUser.email.matches(EMAIL_REGEX) &&
      newUser.phoneNumber.matches(PHONE_NUMBER_REGEX) &&
      newUser.password.matches(PASSWORD_REGEX)
    ) {
      // Go to controller and handle request
      Response response = UserController.controllerCreateUser(newUser);
      sendResponse(response);
    } else {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
    }
  }

  private void routeUpdateUserDetails() {
    if (!authorization.getIsValid()) {
      sendResponse(401, AUTHENTICATION_ERROR, Response.ResponseType.TEXT);
      return;
    }

    User user;
    try {
      user = (User) decodeJson(User.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    // Validate provided information with regexes
    // Might not be definitive solution but better than nothing.
    if (
      (user.username == null || user.username.matches(USERNAME_REGEX)) &&
      (user.firstName == null || user.firstName.matches(NAME_REGEX)) &&
      (user.lastName == null || user.lastName.matches(NAME_REGEX)) &&
      (user.email == null || user.email.matches(EMAIL_REGEX)) &&
      (
        user.phoneNumber == null || user.phoneNumber.matches(PHONE_NUMBER_REGEX)
      ) &&
      (user.password == null || user.password.matches(PASSWORD_REGEX))
    ) {
      // Go to controller and handle request
      Response response = UserController.controllerUpdateUserDetails(
        user,
        authorization
      );
      sendResponse(response);
    } else {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
    }
  }

  private void routeLogin() {
    User user;
    try {
      user = (User) decodeJson(User.class);
    } catch (Exception e) {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
      return;
    }

    // Validate provided information with regexes
    // Might not be definitive solution but better than nothing.
    if (
      user.email.matches(EMAIL_REGEX) && user.password.matches(PASSWORD_REGEX)
    ) {
      // Go to controller and handle request
      Response response = UserController.controllerLogin(
        user.email,
        user.password
      );
      sendResponse(response);
    } else {
      sendResponse(400, API_PARAMETER_ERROR, Response.ResponseType.TEXT);
    }
  }
}
