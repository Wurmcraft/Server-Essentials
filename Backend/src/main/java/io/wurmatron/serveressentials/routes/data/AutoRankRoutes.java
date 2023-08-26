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
import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.routes.RouteUtils;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAutoRank;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AutoRankRoutes {

  @OpenApi(
      summary = "Create a new auto-rank",
      description = "Create a new auto-rank",
      tags = {"Auto-Rank"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = AutoRank.class)},
          required = true,
          description = "Auto-Rank Information used to create the new entry"),
      responses = {
          @OpenApiResponse(
              status = "201",
              content = {@OpenApiContent(from = AutoRank.class)},
              description = "Auto-Rank has been created successfully, autoRankID is also returned"),
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
              status = "409",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Auto-Rank already exists"),
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
      path = "api/autorank",
      method = "POST",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler create =
      ctx -> {
        try {
          AutoRank autoRank = GSON.fromJson(ctx.body(), AutoRank.class);
          if (isValidAutoRank(ctx, autoRank)) {
            // Check for existing
            AutoRank existing = SQLCacheAutoRank.get(autoRank.rank);
            if (existing == null) {
              // Create new AutoRank
              autoRank = SQLCacheAutoRank.create(autoRank);
              if (autoRank == null) {
                ctx.status(500)
                    .result(
                        response("AutoRank Failed to create",
                            "Autorank has failed to be created"));
              }
              ctx.status(201).result(GSON.toJson(autoRank));
            } else {
              ctx.status(409)
                  .result(
                      response("AutoRank Exists", "AutoRank with the same rank exists!"));
            }
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(
                  response("Invalid JSON", "Failed to parse the body into an AutoRank"));
        }
      };

  @OpenApi(
      summary = "Override an existing auto-rank",
      description = "Override an existing auto-rank",
      tags = {"Auto-Rank"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = AutoRank.class)},
          required = true,
          description = "Auto-Rank Information used to update the existing entry"),
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = AutoRank.class)},
              description = "Auto-Rank has been updated successfully"),
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
              status = "404",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Auto-Rank does not exist"),
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
      path = "api/autorank/{rank}",
      method = "PUT",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler override =
      ctx -> {
        try {
          AutoRank autoRank = GSON.fromJson(ctx.body(), AutoRank.class);
          String rank = ctx.pathParam("rank");
          if (!rank.equals(autoRank.rank)) {
            ctx.status(400)
                .result(response("Bad Request", "Path ID and Body ID don't match"));
            return;
          }
          if (isValidAutoRank(ctx, autoRank)) {
            // Check for existing
            AutoRank existing = SQLCacheAutoRank.get(autoRank.rank);
            if (existing != null) {
              // Update Existing Autorank
              SQLCacheAutoRank.update(
                  autoRank,
                  new String[]{
                      "play_time", "next_rank", "currency_name", "currency_amount",
                      "special_events"
                  });
              ctx.status(200).result(GSON.toJson(autoRank));
            } else {
              ctx.status(404)
                  .result(
                      response(
                          "AutoRank does not exists",
                          "AutoRank with the rank does not exist!"));
            }
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(
                  response("Invalid JSON", "Failed to parse the body into an AutoRank"));
        }
      };

  @OpenApi(
      summary = "Get an existing auto-ranks specific data based on its Name",
      description = "Get an existing auto-ranks specific data based on its Name",
      tags = {"Auto-Rank"},
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = AutoRank.class)},
              description = "Requested AutoRank is returned"),
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
              status = "404",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Auto-Rank with the provided ID does not exist"),
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
  @Route(path = "api/autorank/{name}/{data}", method = "GET")
  public static Handler getDataSpecific =
      ctx -> {
        String name = ctx.pathParam("name");
        if (!name.isEmpty()) {
          AutoRank autoRank = SQLCacheAutoRank.get(name);
          if (autoRank != null) {
            String field = convertPathToField(ctx.pathParam("data"));
            if (field != null) {
              Field autoRankField = autoRank.getClass().getField(field);
              autoRank = RouteUtils.wipeAllExceptField(autoRank.clone(), autoRankField);
              ctx.status(200).result(GSON.toJson(autoRank));
            } else {
              ctx.status(400)
                  .result(
                      response(
                          "Bad Request",
                          ctx.pathParam("data") + " is not a valid path param"));
            }
          } else {
            ctx.status(404)
                .result(
                    response("Invalid ID", "No AutoRank Exists with the provided ID"));
          }
        } else {
          ctx.status(400).result(response("Bad Request", "ID must be 0 or greater"));
        }
      };

  @OpenApi(
      summary = "Get an existing auto-ranks",
      description = "Get an existing auto-ranks ",
      tags = {"Auto-Rank"},
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = AutoRank.class)},
              description = "Requested AutoRank is returned"),
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
              status = "404",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Auto-Rank with the provided ID does not exist"),
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
  @Route(path = "api/autorank/{name}", method = "GET")
  public static Handler getData =
      ctx -> {
        String name = ctx.pathParam("name");
        if (!name.isEmpty()) {
          AutoRank autoRank = SQLCacheAutoRank.get(name);
          if (autoRank != null) {
            ctx.status(200).result(GSON.toJson(autoRank));
          } else {
            ctx.status(404)
                .result(response("Invalid Name",
                    "No AutoRank exists with the provided Name"));
          }
        } else {
          ctx.status(400).result(response("Bad Request", "Name must not be empty"));
        }
      };

  @OpenApi(
      summary = "Get existing auto-ranks with query filters",
      description = "Get existing auto-ranks with query filters",
      tags = {"Auto-Rank"},
      queryParams = {
          @OpenApiParam(name = "rank", description = "Rank the user currently has"),
          @OpenApiParam(name = "next", description = "The rank the user will rank-up into"),
          @OpenApiParam(name = "playtime", description = "Total Playtime until rank-up"),
          @OpenApiParam(
              name = "playtime",
              description = "Total Playtime until rank-up",
              type = Long.class),
          @OpenApiParam(name = "currency", description = "Name of the currency used to rank-up"),
          @OpenApiParam(
              name = "currency-amount",
              description = "Amount of the currency required to rank-up",
              type = Double.class),
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = AutoRank[].class)},
              description = "Auto-Ranks matching the filter are returned"),
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
              status = "404",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Auto-Rank with the provided ID does not exist"),
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
  @Route(path = "api/autorank", method = "GET")
  public static Handler get =
      ctx -> {
        String sql = createSQLForUsersWithFilters(ctx);
        // Send Request and Process
        List<AutoRank> accounts = SQLDirect.queryArray(sql, new AutoRank());
        ctx.status(200).result(GSON.toJson(accounts.toArray(new AutoRank[0])));
      };

  @OpenApi(
      summary = "Update an existing auto-rank",
      description = "Update an existing auto-rank",
      tags = {"Auto-Rank"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = AutoRank.class)},
          required = true,
          description = "Auto-Rank Information used to update the existing entry"),
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = AutoRank.class)},
              description = "Auto-Rank has been updated successfully"),
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
              status = "404",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Auto-Rank does not exist"),
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
      path = "api/autorank/{name}/{data}",
      method = "PATCH",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler patch =
      ctx -> {
        try {
          AutoRank autorank = GSON.fromJson(ctx.body(), AutoRank.class);
          String name = ctx.pathParam("name");
          if (!name.isEmpty()) {
            AutoRank cacheAutoRank = SQLCacheAutoRank.get(name);
            if (cacheAutoRank != null) {
              String field = convertPathToField(ctx.pathParam("data"));
              if (field != null) {
                Field arField = cacheAutoRank.getClass().getDeclaredField(field);
                arField.set(cacheAutoRank, arField.get(autorank));
                if (isValidAutoRank(ctx, cacheAutoRank)) {
                  ctx.status(200)
                      .result(GSON.toJson(filterBasedOnPerms(ctx, cacheAutoRank)));
                } else {
                  ctx.status(500)
                      .result(
                          response(
                              "AutoRank Error",
                              "AutoRank has failed to be validated!, Full Update / Put is required"));
                }
              } else {
                ctx.status(400)
                    .result(
                        response(
                            "Bad Request",
                            ctx.pathParam("data") + " is not a valid path param"));
              }
            } else {
              ctx.status(404)
                  .result(
                      response("Invalid ID", "No AutoRank Exists with the provided ID"));
            }
          } else {
            ctx.status(400).result(response("Bad Request", "Name must not be empty"));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(
                  response("Invalid JSON", "Failed to parse the body into an AutoRank"));
        }
      };

  @OpenApi(
      summary = "Delete an existing auto-rank",
      description = "Delete an existing auto-rank",
      tags = {"Auto-Rank"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = AutoRank.class)},
              description = "Auto-Rank has been updated deleted"),
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
              status = "409",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description = "Rank already exists"),
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
      path = "api/autorank/{name}",
      method = "DELETE",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler delete =
      ctx -> {
        String name = ctx.pathParam("name");
        if (!name.isEmpty()) {
          AutoRank autoRank = SQLCacheAutoRank.get(name);
          if (autoRank != null) {
            boolean deleted = SQLCacheAutoRank.delete(autoRank.rank);
            if (deleted) {
              ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, autoRank)));
            } else {
              ctx.status(500)
                  .result(
                      response(
                          "Delete Failed",
                          "Failed to delete autorank '" + autoRank.rank + "'"));
            }
          } else {
            ctx.status(404)
                .result(
                    response("Invalid ID", "No AutoRank Exists with the provided ID"));
          }
        } else {
          ctx.status(400).result(response("Bad Request", "Name must not be empty"));
        }
      };

  /**
   * Checks if the provided autorank is valid, if not respond with the error
   *
   * @param context context of the message
   * @param autoRank instance of the provided outrank to check
   */
  public static boolean isValidAutoRank(Context context, AutoRank autoRank) {
    List<MessageResponse> errors = new ArrayList<>();
    // Check Rank
    if (autoRank.rank == null || autoRank.rank.trim().isEmpty()) {
      errors.add(new MessageResponse("Bad Request", "Invalid / Empty Rank"));
    }
    // Check Next Rank
    if (autoRank.next_rank == null || autoRank.next_rank.trim().isEmpty()) {
      errors.add(new MessageResponse("Bad Request", "Invalid / Empty Next-Rank"));
    }
    // Check playtime
    if (autoRank.play_time != null && autoRank.play_time < 0) {
      errors.add(
          new MessageResponse("Bad Request",
              "Invalid Playtime, Must be equal or greater than 0"));
    }
    if (autoRank.currency_name != null
        && !autoRank.currency_name.trim().isEmpty()
        && autoRank.currency_amount != null
        && autoRank.currency_amount < 0) {
      errors.add(
          new MessageResponse(
              "Bad Request", "Invalid Currency Amount, Must be equal or greater than 0"));
    }
    if (errors.size() == 0) {
      return true;
    } else {
      context.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
    }
    return false;
  }

  /**
   * Filters / Removes data based on the users permission / authentication
   *
   * @param ctx context to get the user from, user authentication
   * @param autoRank instance to remove the data from before returning
   * @return filtered version of the autoRank instance with data removed (if required)
   */
  private static AutoRank filterBasedOnPerms(Context ctx, AutoRank autoRank) {
    Route.RestRoles role = EndpointSecurity.getRole(ctx);
    if (role.equals(Route.RestRoles.DEV) || role.equals(Route.RestRoles.SERVER)) {
      return autoRank;
    }
    AutoRank clone = autoRank.clone();
    if (role.equals(Route.RestRoles.USER)) {
      // TODO Based on SystemPerms
    }
    clone.special_events = null;
    return clone;
  }

  /**
   * Converts the endpoint PathParm into he internal data name, used for reflection
   *
   * @param data PathParam provided by the user via the endpoint
   */
  public static String convertPathToField(String data) {
    if (data.equalsIgnoreCase("rank") || data.equals("name")) {
      return "rank";
    } else if (data.equalsIgnoreCase("next-rank")) {
      return "next_rank";
    } else if (data.equalsIgnoreCase("playtime")) {
      return "playtime";
    } else if (data.equalsIgnoreCase("currency")) {
      return "currency_name";
    } else if (data.equalsIgnoreCase("currency-amount")) {
      return "currency_amount";
    } else if (data.equalsIgnoreCase("special-event")
        || data.equalsIgnoreCase("special-events")
        || data.equalsIgnoreCase("event")) {
      return "special_events";
    }
    return null;
  }

  /**
   * Generates a SQL Statement for get users with filters applied
   *
   * @param ctx context to get the information from the user
   * @return sql statement for autorank lookup
   */
  private static String createSQLForUsersWithFilters(Context ctx) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT * FROM " + SQLCacheAutoRank.AUTORANK_TABLE + " WHERE ");
    // Verify, Check and Apply Rank Filter
    String rank = ctx.queryParam("rank");
    if (rank != null && !rank.trim().isEmpty()) {
      sqlBuilder.append("rank LIKE '").append(rank).append("%' AND ");
    }
    // Verify, Check and Apply Next-Rank Filter
    String nextRank = ctx.queryParam("next");
    if (nextRank != null && !nextRank.trim().isEmpty()) {
      sqlBuilder.append("next_rank LIKE '").append(nextRank).append("%' AND ");
    }
    // Verify, Check and Apply playtimeFilter
    String playtime = ctx.queryParam("playtime");
    if (playtime != null && !playtime.trim().isEmpty()) {
      sqlBuilder.append("play_time='").append(playtime).append("' AND ");
    }
    // Verify, Check and Apply Currency Filter
    String currency = ctx.queryParam("currency");
    if (currency != null && !currency.trim().isEmpty()) {
      sqlBuilder.append("currency_name LIKE '").append(currency).append("%' AND ");
    }
    // Verify, Check and Apply currency amount Filter
    String amount = ctx.queryParam("currency-amount");
    if (amount != null && !amount.trim().isEmpty()) {
      sqlBuilder.append("currency_amount='").append(amount).append("' AND ");
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
}
