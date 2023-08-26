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
import io.wurmatron.serveressentials.models.LogEntry;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;
import io.wurmatron.serveressentials.sql.routes.SQLLogging;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoggingRoutes {

  @OpenApi(
      summary = "Create a new logging event",
      description = "Create a new logging event",
      tags = {"Logging"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = LogEntry.class)},
          required = true,
          description = "Information about the log event"),
      responses = {
          @OpenApiResponse(
              status = "201",
              content = {@OpenApiContent(from = LogEntry.class)},
              description = "Log Entry has been created successfully,"),
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
      path = "api/logging",
      method = "POST",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler create =
      ctx -> {
        try {
          LogEntry newLogEntry = GSON.fromJson(ctx.body(), LogEntry.class);
          if (isValidLogEntry(ctx, newLogEntry)) {
            SQLLogging.create(newLogEntry);
            ctx.status(201).result(GSON.toJson(newLogEntry));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse body into Log Entry"));
        }
      };

  @OpenApi(
      summary = "Filter the log entries based on your query",
      description = "Filter the log entries based on your query",
      tags = {"Logging"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      queryParams = {
          @OpenApiParam(
              name = "server-id",
              description = "ID of the server where this event took place"),
          @OpenApiParam(name = "action", description = "Type of action that has taken place"),
          @OpenApiParam(
              name = "uuid",
              description = "UUID of the user / account that caused this event"),
          @OpenApiParam(
              name = "x",
              description = "X Position that this event took place",
              type = Integer.class),
          @OpenApiParam(
              name = "y",
              description = "Y Position that this event took place",
              type = Integer.class),
          @OpenApiParam(
              name = "z",
              description = "Z Position that this event took place",
              type = Integer.class),
          @OpenApiParam(
              name = "dim",
              description = "Dimension that this event took place",
              type = Integer.class)
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = LogEntry[].class)},
              description = "Log Entry's that fit into your required filters (query)"),
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
  @Route(path = "api/logging", method = "GET")
  public static Handler get =
      ctx -> {
        String sql = createSQLForLogEntryWithFilters(ctx);
        // Send Request and Process
        List<LogEntry> entries = SQLDirect.queryArray(sql, new LogEntry());
        ctx.status(200).result(GSON.toJson(entries.toArray(new LogEntry[0])));
      };

  @OpenApi(
      summary = "Update an existing log event",
      description = "Update an existing log event",
      tags = {"Logging"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = LogEntry.class)},
          required = true,
          description = "Information about the log event"),
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = LogEntry.class)},
              description = "Log Entry has been successfully updated"),
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
      path = "api/logging",
      method = "PUT",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler override =
      ctx -> {
        try {
          LogEntry updatedLogEntry = GSON.fromJson(ctx.body(), LogEntry.class);
          if (isValidLogEntry(ctx, updatedLogEntry)) {
            SQLLogging.update(updatedLogEntry,
                new String[]{"actionData", "x", "y", "z", "dim"});
            List<LogEntry> entrys =
                SQLLogging.get(
                    updatedLogEntry.server_id, updatedLogEntry.action_type,
                    updatedLogEntry.uuid);
            for (LogEntry e : entrys) {
              if (e.x.equals(updatedLogEntry.x)
                  && e.y.equals(updatedLogEntry.y)
                  && e.z.equals(updatedLogEntry.z)
                  && e.dim.equals(updatedLogEntry.dim)) {
                ctx.status(200).result(GSON.toJson(e));
                return;
              }
            }
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse body into Log Entry"));
        }
      };

  @OpenApi(
      summary = "Remove an existing log event",
      description = "Remove an existing log event",
      tags = {"Logging"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to used for authentication within the rest API",
              required = true)
      },
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = LogEntry.class)},
          required = true,
          description = "The event entry you want to remove"),
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = LogEntry.class)},
              description = "Log Entry has been successfully deleted"),
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
      path = "api/logging",
      method = "DELETE",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler delete =
      ctx -> {
        try {
          LogEntry logEntryToDelete = GSON.fromJson(ctx.body(), LogEntry.class);
          if (isValidLogEntry(ctx, logEntryToDelete)) {
            List<LogEntry> entrys =
                SQLLogging.get(
                    logEntryToDelete.server_id,
                    logEntryToDelete.action_type,
                    logEntryToDelete.uuid);
            for (LogEntry e : entrys) {
              if (e.timestamp.equals(logEntryToDelete.timestamp)
                  && e.action_type.equals(logEntryToDelete.action_type)
                  && e.server_id.equals(logEntryToDelete.server_id)) {
                SQLLogging.delete(
                    logEntryToDelete.server_id,
                    logEntryToDelete.action_type,
                    logEntryToDelete.uuid,
                    logEntryToDelete.timestamp);
                ctx.status(200).result(GSON.toJson(e));
                return;
              }
            }
            ctx.status(404).result(response("Not Found", "Log Entry not found"));
          }
        } catch (JsonParseException e) {
          ctx.status(422)
              .result(response("Invalid JSON", "Failed to parse body into Log Entry"));
        }
      };

  /**
   * Verify a log entry is valid
   *
   * @param ctx instance of the message
   * @param entry Log entry to be tested
   * @return if the log entry is valid
   */
  public static boolean isValidLogEntry(Context ctx, LogEntry entry) {
    List<MessageResponse> errors = new ArrayList<>();
    // Verify ServerID
    if (entry.server_id == null || entry.server_id.trim().isEmpty()) {
      errors.add(
          new MessageResponse("Invalid ServerID", "serverID must be non-null / empty"));
    }
    // Verify actionType
    if (entry.action_type == null || entry.action_type.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid ActionType",
          "actionType must be non-null / empty"));
    }
    // Verify UUID
    if (entry.uuid == null || entry.uuid.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid UUID", "uuid must be a valid uuid"));
    }
    try {
      if (entry.uuid != null) {
        UUID.fromString(entry.uuid);
      }
    } catch (Exception e) {
      errors.add(new MessageResponse("Invalid UUID", "uuid must be a valid uuid"));
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
  private static String createSQLForLogEntryWithFilters(Context ctx) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT * FROM " + SQLLogging.LOGGING_TABLE + " WHERE ");
    // Verify, Check and Apply ServerID Filter
    String serverID = ctx.queryParam("server-id");
    if (serverID != null && !serverID.trim().isEmpty()) {
      sqlBuilder.append("server_id LIKE '").append(serverID).append("%' AND ");
    }
    // Verify, Check and Apply action Filter
    String action = ctx.queryParam("action");
    if (action != null && !action.trim().isEmpty()) {
      sqlBuilder.append("action_type LIKE '").append(action).append("%' AND ");
    }
    // Verify, Check and Apply UUID Filter
    String uuid = ctx.queryParam("uuid");
    if (uuid != null && !uuid.trim().isEmpty()) {
      sqlBuilder.append("uuid LIKE '").append(uuid).append("%' AND ");
    }
    // Verify, Check and Apply x Filter
    String x = ctx.queryParam("x");
    if (x != null && !x.trim().isEmpty()) {
      sqlBuilder.append("x=").append(x).append(" AND ");
    }
    // Verify, Check and Apply y Filter
    String y = ctx.queryParam("y");
    if (y != null && !y.trim().isEmpty()) {
      sqlBuilder.append("y=").append(y).append(" AND ");
    }
    // Verify, Check and Apply z Filter
    String z = ctx.queryParam("z");
    if (z != null && !z.trim().isEmpty()) {
      sqlBuilder.append("z=").append(z).append(" AND ");
    }
    // Verify, Check and Apply dim Filter
    String dim = ctx.queryParam("dim");
    if (dim != null && !dim.trim().isEmpty()) {
      sqlBuilder.append("dim=").append(dim).append(" AND ");
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
