package io.wurmatron.server_essentials.backend.rest.crud;

import io.javalin.core.validation.BodyValidator;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.server_essentials.backend.db.DatabaseConnector;
import io.wurmatron.server_essentials.backend.model.db.UserAccount;
import io.wurmatron.server_essentials.backend.model.rest.RestResponse;
import io.wurmatron.server_essentials.backend.rest.Route;
import java.util.Objects;
import java.util.UUID;

public class UserRoutes {

  @OpenApi(
      description = "Create a new user account",
      summary = "Create a new user account with the provided information",
      deprecated = false,
      tags = {"User"},
      headers = {},
      requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = UserAccount.class), description = "User account to be created"),
      responses = {
          @OpenApiResponse(status = "201", content = @OpenApiContent(from = RestResponse.class), description = "User account has been created"),
          @OpenApiResponse(status = "400", content = @OpenApiContent(from = RestResponse.class), description = "There is an error with the provided user account"),
          @OpenApiResponse(status = "401", content = @OpenApiContent(from = RestResponse.class), description = "No credentials where provided to preform this action"),
          @OpenApiResponse(status = "403", content = @OpenApiContent(from = RestResponse.class), description = "The provided credentials do not have permission to preform this action"),
          @OpenApiResponse(status = "409", content = @OpenApiContent(from = RestResponse.class), description = "A user account with the provided information already exists"),
          @OpenApiResponse(status = "429", content = @OpenApiContent(from = RestResponse.class), description = "The provided credentials have created to many user accounts within a given time frame"),
          @OpenApiResponse(status = "500", content = @OpenApiContent(from = RestResponse.class), description = "The api had an internal server error, see the response for more information"),
      }
  )
  @Route(path = "/api/users", method = "POST")
  public static Handler createUser = ctx -> {
    UserAccount userAccount = validateUserAccount(ctx.bodyValidator(UserAccount.class));
    Objects.requireNonNull(DatabaseConnector.getSession()).getCurrentSession()
        .beginTransaction();
    DatabaseConnector.getSession().getCurrentSession()
        .save(userAccount);
    DatabaseConnector.getSession().getCurrentSession().getTransaction().commit();
    ctx.status(201);
  };

  private static UserAccount validateUserAccount(BodyValidator<UserAccount> account) {
    return account.check(usr -> usr.getUuid() != null, "UUID must not be null")
        .check(usr -> usr.getUuid().length() > 0, "UUID must not be empty")
        .check(usr -> {
          try {
            UUID.fromString(usr.getUuid());
            return true;
          } catch (Exception e) {
            return false;
          }
        }, "UUID must be valid")
        .check(usr -> usr.getLastUsername().length() > 0, "Username must not be empty")
        .get();
  }
}
