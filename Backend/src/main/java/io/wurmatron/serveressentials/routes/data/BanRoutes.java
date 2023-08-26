/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.routes.data;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Ban;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheBan;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanRoutes {

  @OpenApi(
      summary = "Creates a new user with the provided information",
      description = "Creates a new user with the provided information",
      tags = {"Ban"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = Ban.class)},
          required = true,
          description = "Ban information to be used to create the ban entry"),
      responses = {
          @OpenApiResponse(
              status = "201",
              content = {@OpenApiContent(from = Ban.class)},
              description = "Ban entry has been created successfully,"),
          @OpenApiResponse(
              status = "400",
              content = {@OpenApiContent(from = MessageResponse[].class)},
              description = "One or more of the provided values, has failed to validate!"),
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
      path = "api/ban",
      method = "POST",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler create =
      ctx -> {
        try {
          Ban newBan = GSON.fromJson(ctx.body(), Ban.class);
          if (isValidBan(ctx, newBan)) {
            Ban ban = SQLCacheBan.create(newBan);
            if (ban == null) {
              ctx.status(500)
                  .result(response("Ban Failed",
                      "Failed to create ban for '" + newBan.uuid + "'"));
              return;
            }
            ctx.status(201).result(GSON.toJson(ban));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse the body into an Ban"));
        }
      };

  @OpenApi(
      summary = "Get a list ban entry based on the provided query",
      description = "Get a list ban entry based on the provided query",
      tags = {"Ban"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      queryParams = {
          @OpenApiParam(name = "uuid", description = "UUID of the banned account"),
          @OpenApiParam(name = "discord", description = "ID of the user on discord"),
          @OpenApiParam(name = "ban-type", description = "Type of the ban entry"),
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = Ban[].class)},
              description = "Ban entry filtered on the query"),
          @OpenApiResponse(
              status = "400",
              content = {@OpenApiContent(from = MessageResponse[].class)},
              description = "One or more of the provided values, has failed to validate!"),
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
              status = "422",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Unable to process, due to invalid format / json"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(path = "api/ban", method = "GET")
  public static Handler get =
      ctx -> {
        String sql = createSQLForUsersWithFilters(ctx);
        // Send Request and Process
        List<Ban> bans = SQLDirect.queryArray(sql, new Ban());
        ctx.status(200).result(GSON.toJson(bans.toArray(new Ban[0])));
      };

  /**
   * Generates a SQL Statement for get users with filters applied
   *
   * @param ctx context to get the information from the user
   * @return sql statement for ban lookup
   */
  private static String createSQLForUsersWithFilters(Context ctx) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT * FROM " + SQLCacheBan.BAN_TABLE + " WHERE ");
    // Verify, Check and Apply UUID Filter
    String uuid = ctx.queryParam("uuid");
    if (uuid != null && !uuid.trim().isEmpty()) {
      sqlBuilder.append("uuid LIKE '").append(uuid).append("%' AND ");
    }
    // Verify, Check and Apply Discord Filter
    String discord = ctx.queryParam("discord");
    if (discord != null && !discord.trim().isEmpty()) {
      sqlBuilder.append("discordID LIKE '").append(discord).append("%' AND ");
    }
    // Verify, Check and Apply Ban Type Filter
    String type = ctx.queryParam("ban-type");
    if (type != null && !type.trim().isEmpty()) {
      sqlBuilder.append("banType LIKE '").append(type).append("%' AND ");
    }
    // Finalize SQL
    sqlBuilder.append(";");
    String sql = sqlBuilder.toString();
    if (sql.endsWith("WHERE ;")) {
      sql = sql.substring(0, sql.length() - 7);
    }
    if (sql.endsWith(" AND ;")) {
      sql = sql.substring(0, sql.length() - 5);
    }
    return sql;
  }

  @OpenApi(
      summary = "Get a ban entry based on its ID",
      description = "Get a ban entry based on its ID",
      tags = {"Ban"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = Ban.class)},
              description = "Ban entry related to the provided id"),
          @OpenApiResponse(
              status = "400",
              content = {@OpenApiContent(from = MessageResponse[].class)},
              description = "One or more of the provided values, has failed to validate!"),
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
              status = "422",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Unable to process, due to invalid format / json"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(path = "api/ban/{id}", method = "GET")
  public static Handler getID =
      ctx -> {
        try {
          long id = Long.parseLong(ctx.pathParam("id"));
          Ban ban = SQLCacheBan.get(id);
          if (ban != null) {
            ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, ban)));
          } else {
            ctx.status(404)
                .result(
                    response("Invalid Ban", "Ban with the provided ID does not exist"));
          }
        } catch (NumberFormatException e) {
          ctx.status(400)
              .result(response("Bad Request", "Invalid Path Param, Must be a number"));
        }
      };

  @OpenApi(
      summary = "Update an existing ban entry",
      description = "Update an existing ban entry",
      tags = {"Ban"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = Ban.class)},
          required = true,
          description = "Ban information to be used to create the ban entry"),
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = Ban.class)},
              description = "Ban entry has been updated successfully,"),
          @OpenApiResponse(
              status = "400",
              content = {@OpenApiContent(from = MessageResponse[].class)},
              description = "One or more of the provided values, has failed to validate!"),
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
      path = "api/ban/{id}",
      method = "PUT",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler override =
      ctx -> {
        try {
          Ban banUpdate = GSON.fromJson(ctx.body(), Ban.class);
          try {
            long banID = Long.parseLong(ctx.pathParam("id"));
            if (banID >= 0) {
              if (banID == banUpdate.ban_id) {
                SQLCacheBan.update(banUpdate, SQLCacheBan.getColumns());
                ctx.status(200).result(GSON.toJson(SQLCacheBan.get(banID)));
              } else {
                ctx.status(400)
                    .result(response("ID Mismatch", "Path and body ID don't match"));
              }
            } else {
              ctx.status(400)
                  .result(
                      response("Bad Path Param",
                          "ID must be a number, equal or greater than 0"));
            }
          } catch (NumberFormatException e) {
            ctx.status(400)
                .result(response("Bad Path Param",
                    "ID must be a number, equal or greater than 0"));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse the body into an Ban"));
        }
      };

  @OpenApi(
      summary = "Delete an existing ban entry",
      description = "Delete an existing ban entry",
      tags = {"Ban"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = Ban.class)},
              description = "Ban entry has been deleted successfully,"),
          @OpenApiResponse(
              status = "400",
              content = {@OpenApiContent(from = MessageResponse[].class)},
              description = "One or more of the provided values, has failed to validate!"),
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
      path = "api/ban/{id}",
      method = "DELETE",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler delete =
      ctx -> {
        try {
          long id = Long.parseLong(ctx.pathParam("id"));
          Ban ban = SQLCacheBan.get(id);
          if (ban != null) {
            SQLCacheBan.delete(id);
            ctx.status(200).result(GSON.toJson(ban));
          } else {
            ctx.status(404)
                .result(
                    response("Invalid Ban", "Ban with the provided ID does not exist"));
          }
        } catch (NumberFormatException e) {
          ctx.status(400)
              .result(response("Bad Request", "Invalid Path Param, Must be a number"));
        }
      };

  /**
   * Checks if the provided instance is valid
   *
   * @param context context of the message
   * @param ban instance of the ban to be checked
   * @return if the ban is valid or not
   */
  public static boolean isValidBan(Context context, Ban ban) {
    List<MessageResponse> errors = new ArrayList<>();
    // Check UUID
    try {
      UUID.fromString(ban.uuid);
    } catch (Exception e) {
      errors.add(new MessageResponse("Bad UUID", "Empty or Invalid UUID"));
    }
    // Check bannedBY
    if (ban.banned_by == null || ban.banned_by.trim().isEmpty()) {
      errors.add(
          new MessageResponse("Missing Banned-By",
              "A player must be banned by someone or thing"));
    }
    // Check banned by Type
    if (ban.banned_by_type == null || ban.banned_by_type.trim().isEmpty()) {
      errors.add(
          new MessageResponse("Invalid Banned-By Type",
              "A ban must be created by something"));
    }
    if (ban.ban_type == null || ban.ban_type.trim().isEmpty()) {
      errors.add(new MessageResponse("No Ban Type", "You must specify the type of ban"));
    }
    if (!ban.ban_status) {
      errors.add(new MessageResponse("Invalid Ban State", "A new ban must be active"));
    }
    if (errors.size() == 0) {
      return true;
    }
    context.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
    return false;
  }

  /**
   * Filters / Removes data based on the users permission / authentication
   *
   * @param ctx context to get the user from, user authentication
   * @param ban instance to remove the data from before returning
   * @return filtered version of the autoRank instance with data removed (if required)
   */
  private static Ban filterBasedOnPerms(Context ctx, Ban ban) {
    Route.RestRoles role = EndpointSecurity.getRole(ctx);
    if (role.equals(Route.RestRoles.DEV) || role.equals(Route.RestRoles.SERVER)) {
      return ban;
    }
    Ban clone = ban.clone();
    if (role.equals(Route.RestRoles.USER)) {
      // TODO Based on SystemPerms
    }
    ban.ip = null;
    ban.discord_id = null;
    ban.ban_data = null;
    ban.banned_by = null;
    ban.banned_by_type = null;
    return clone;
  }
}
