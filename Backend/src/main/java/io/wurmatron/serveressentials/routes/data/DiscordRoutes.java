package io.wurmatron.serveressentials.routes.data;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

import com.google.gson.JsonParseException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.serveressentials.discord.BotCommands;
import io.wurmatron.serveressentials.models.DiscordVerify;
import io.wurmatron.serveressentials.models.Donator;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.routes.Route.RestRoles;
import io.wurmatron.serveressentials.sql.routes.SQLCacheDonator;

public class DiscordRoutes {

  @OpenApi(
      summary = "Verify a username and token match, links to previously run discord command",
      description = "Verify a token is valid",
      tags = {"Discord"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = DiscordVerify.class)},
          required = true,
          description = "Details about the user's verification"),
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = DiscordVerify.class)},
              description = "Full token data is returned and verified"),
          @OpenApiResponse(
              status = "400",
              content = {@OpenApiContent(from = MessageResponse[].class)},
              description = "Missing Username or Auth token"),
          @OpenApiResponse(
              status = "401",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "You are missing an authorization token"),
          @OpenApiResponse(
              status = "403",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "Forbidden, Your provided auth token does not have permission to do this"),
          @OpenApiResponse(
              status = "404",
              description = "Data is invalid"),
          @OpenApiResponse(
              status = "422",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Unable to process, due to invalid format / json"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(
      path = "api/discord",
      method = "POST",
      roles = {RestRoles.SERVER, RestRoles.DEV}
  )
  public static Handler verifyCode =
      ctx -> {
        try {
          DiscordVerify verify = GSON.fromJson(ctx.body(), DiscordVerify.class);
          if (verify.uuid != null && verify.username != null && verify.token != null) {
            if (BotCommands.verifyCodes.contains(verify.username.toUpperCase())
                && BotCommands.verifyCodes.get(
                verify.username.toUpperCase())[2].equalsIgnoreCase(verify.token)) {
              ctx.status(200).result(GSON.toJson(
                  new DiscordVerify(verify.token, verify.uuid, verify.username,
                      BotCommands.verifyCodes.get(verify.username.toUpperCase())[0],
                      BotCommands.verifyCodes.get(verify.username.toUpperCase())[1])));
              BotCommands.verifyCodes.remove(verify.username.toUpperCase());
            } else {
              ctx.status(404);
            }

          } else {
            ctx.status(400).result(
                response("Missing Data", "uuid, username and token are required"));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Cannot parse body into DiscordVerify"));
        }
      };

}
