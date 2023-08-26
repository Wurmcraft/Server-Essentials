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
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.TrackedStat;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;
import io.wurmatron.serveressentials.sql.routes.SQLStatistics;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatisticRoutes {

  @OpenApi(
      summary = "Creates a new statistic entry",
      description = "Creates a new statistical entry into the database",
      tags = {"Statistics"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      responses = {
          @OpenApiResponse(
              status = "201",
              content = {@OpenApiContent(from = TrackedStat.class)},
              description = "Statistic Entry has been created successfully"),
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
              description = "Stat Entry already exists"),
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
      path = "api/statistics",
      method = "POST",
      roles = {Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler create =
      ctx -> {
        try {
          TrackedStat newStat = GSON.fromJson(ctx.body(), TrackedStat.class);
          if (isValidStat(ctx, newStat)) {
            // Check for duplicates
            List<TrackedStat> sqlStats = SQLStatistics.get(newStat.server_id,
                newStat.uuid);
            for (TrackedStat stat : sqlStats) {
              if (stat.event_type.equals(newStat.event_type)) {
                ctx.status(409)
                    .result(
                        response(
                            "Stat Exists",
                            "Stat with the same event for the provided user exists"));
                return;
              }
            }
            // Create new entry
            TrackedStat stat = SQLStatistics.create(newStat);
            ctx.status(201).result(GSON.toJson(stat));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse body into Stat Entry"));
        }
      };

  @OpenApi(
      summary = "Override an existing statistic entry",
      description = "Completely overwrite an existing entry in the database",
      tags = {"Statistics"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TrackedStat.class)},
              description = "Statistic Entry has been updated successfully"),
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
              description = "Entry does not exist"),
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
      path = "api/statistics",
      method = "PUT",
      roles = {Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler override =
      ctx -> {
        try {
          TrackedStat updateStat = GSON.fromJson(ctx.body(), TrackedStat.class);
          if (isValidStat(ctx, updateStat)) {
            // Check for duplicates
            List<TrackedStat> sqlStats = SQLStatistics.get(updateStat.server_id,
                updateStat.uuid);
            for (TrackedStat stat : sqlStats) {
              if (stat.event_type.equals(updateStat.event_type)) {
                // Update existing
                SQLStatistics.update(updateStat, new String[]{"eventData"});
                sqlStats = SQLStatistics.get(updateStat.server_id, updateStat.uuid);
                for (TrackedStat s : sqlStats) {
                  if (stat.event_type.equals(updateStat.event_type)) {
                    ctx.status(200).result(GSON.toJson(s));
                    return;
                  }
                }
              }
            }
            ctx.status(404)
                .result(
                    response(
                        "Stat does not exist",
                        "Stat type does not exist for the provided user"));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse body into Stat Entry"));
        }
      };

  @OpenApi(
      summary =
          "Find a specific tracked statistic about a given server or player, via query params",
      description = "Find a specific entry, based on the filters",
      tags = {"Statistics"},
      queryParams = {
          @OpenApiParam(name = "server-id", description = "ID of the server this stat is tracked on"),
          @OpenApiParam(name = "uuid", description = "UUID of the user being tracked"),
          @OpenApiParam(name = "event", description = "Event Type being tracked"),
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TrackedStat[].class)},
              description = "Statistic Entry that match, based on the provided filters"),
          @OpenApiResponse(
              status = "400",
              content = {@OpenApiContent(from = MessageResponse[].class)},
              description = "One or more of the provided values, has failed to validate!"),
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
                  "The server has encountered an error, please contact the server's admin to check the logs"),
      })
  @Route(path = "api/statistics", method = "GET")
  public static Handler get =
      ctx -> {
        String sql = createSQLForStatWithFilters(ctx);
        // Send Request and Process
        List<TrackedStat> trackedStats = SQLDirect.queryArray(sql, new TrackedStat());
        ctx.status(200).result(GSON.toJson(trackedStats.toArray(new TrackedStat[0])));
      };

  @OpenApi(
      summary = "Delete a specific transfer entry",
      description = "Update a specific entry from a transfer entry",
      tags = {"Statistics"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TrackedStat.class)},
              description = "Statistic Entry has been updated deleted"),
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
      path = "api/statistics",
      method = "DELETE",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler delete =
      ctx -> {
        try {
          TrackedStat deleteStat = GSON.fromJson(ctx.body(), TrackedStat.class);
          if (isValidStat(ctx, deleteStat)) {
            // Check for duplicates
            List<TrackedStat> sqlStats = SQLStatistics.get(deleteStat.server_id,
                deleteStat.uuid);
            for (TrackedStat stat : sqlStats) {
              if (stat.event_type.equals(deleteStat.event_type)) {
                SQLStatistics.delete(deleteStat.server_id, deleteStat.uuid,
                    deleteStat.event_type);
                ctx.status(200).result(GSON.toJson(stat));
                return;
              }
            }
            ctx.status(404)
                .result(
                    response(
                        "Stat does not exist",
                        "Stat type does not exist for the provided user"));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse body into Stat Entry"));
        }
      };

  /**
   * Validates if a stat is valid
   *
   * @param ctx context of the message
   * @param stat instance of the stat to be validated
   * @return if a stat is valid or not
   */
  public static boolean isValidStat(Context ctx, TrackedStat stat) {
    List<MessageResponse> errors = new ArrayList<>();
    // Verify EventType
    if (stat.event_type == null || stat.event_type.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid Event Type", "EventType must be non-null"));
    }
    // Verify ServerID
    if (stat.server_id == null || stat.server_id.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid ServerID", "ServerID must be non-null"));
    }
    // Verify UUID
    if (stat.uuid == null || stat.uuid.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid UUID", "UUID must be a valid UUID"));
    }
    try {
      UUID.fromString(stat.uuid);
    } catch (Exception e) {
      if (stat.uuid != null) {
        errors.add(new MessageResponse("Invalid UUID", "UUID must be a valid UUID"));
      }
    }
    if (errors.size() == 0) {
      return true;
    }
    ctx.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
    return false;
  }

  /**
   * Generates a SQL Statement for get users with filters applied
   *
   * @param ctx context to get the information from the user
   * @return sql statement for currency lookup
   */
  private static String createSQLForStatWithFilters(Context ctx) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT * FROM " + SQLStatistics.STATISTICS_TABLE + " WHERE ");
    // Verify, Check and Apply UUID Filter
    String uuid = ctx.queryParam("uuid");
    if (uuid != null && !uuid.trim().isEmpty()) {
      sqlBuilder.append("uuid LIKE '").append(uuid).append("%' AND ");
    }
    // Verify, Check and Apply serverID Filter
    String serverID = ctx.queryParam("server-id");
    if (serverID != null && !serverID.trim().isEmpty()) {
      sqlBuilder.append("serverID LIKE '").append(serverID).append("%' AND ");
    }
    // Verify, Check and Apply eventType Filter
    String event = ctx.queryParam("event");
    if (event != null && !event.trim().isEmpty()) {
      sqlBuilder.append("eventType LIKE '").append(event).append("%' AND ");
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
