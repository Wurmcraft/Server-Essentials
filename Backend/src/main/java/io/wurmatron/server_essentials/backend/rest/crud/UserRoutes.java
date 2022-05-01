package io.wurmatron.server_essentials.backend.rest.crud;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.server_essentials.backend.model.db.UserAccount;
import io.wurmatron.server_essentials.backend.model.rest.RestResponse;
import io.wurmatron.server_essentials.backend.rest.Route;

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
    ctx.status(501);
  };
}
