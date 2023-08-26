/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.routes.data;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;
import static io.wurmatron.serveressentials.routes.RouteUtils.wipeAllExceptField;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheTransfers;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransferRoutes {

  @OpenApi(
      summary = "Creates a new entry with provided information",
      description = "Creates a new entry for the provided user with the provided information",
      tags = {"Transfer"},
      requestBody =
      @OpenApiRequestBody(
          content = {@OpenApiContent(from = TransferEntry.class)},
          required = true,
          description = "Entry Entry information used to create the requested entry"),
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to be used for authentication within the rest api")
      },
      responses = {
          @OpenApiResponse(
              status = "201",
              content = {@OpenApiContent(from = TransferEntry.class)},
              description = "Transfer Entry has been created successfully, rankID is also returned"),
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
              description = "Transfer Entry already exists"),
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
      path = "api/transfer",
      method = "POST",
      roles = {Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler create =
      ctx -> {
        try {
          TransferEntry transferEntry = GSON.fromJson(ctx.body(), TransferEntry.class);
          if (isValidTransferEntry(ctx, transferEntry)) {
            TransferEntry entry = SQLCacheTransfers.create(transferEntry);
            ctx.status(201).result(GSON.toJson(entry));
          }
        } catch (JsonParseException e) {
          ctx.status(400)
              .result(response("Invalid JSON", "Cannot Parse body into Transfer Entry"));
        }
      };

  @OpenApi(
      summary = "Find a list of transfer entries that's filtered based on the provided parameters",
      description =
          "Find a list of transfer entries that's filtered based on the provided parameters",
      tags = {"Transfer"},
      queryParams = {
          @OpenApiParam(
              name = "uuid",
              description = "Full or Partial UUID of the account to filter by"),
          @OpenApiParam(name = "server-id", description = "Full or Partial Server ID to filter by"),
          @OpenApiParam(name = "start-time", description = "Starting Time when the entry was created")
      },
      responses = {
          @OpenApiResponse(
              status = "201",
              content = {@OpenApiContent(from = TransferEntry[].class)},
              description = "List of all the Transfer "),
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
      path = "api/transfer",
      method = "GET",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler get =
      ctx -> {
        String sql = createSQLForTransferEntryWithFilters(ctx);
        // Send Request and Process
        List<TransferEntry> entries = SQLDirect.queryArray(sql, new TransferEntry());
        List<TransferEntry> permedEntries = new ArrayList<>();
        for (TransferEntry entry : entries) {
          permedEntries.add(filterBasedOnPerms(ctx, entry));
        }
        ctx.status(200).result(GSON.toJson(permedEntries.toArray(new TransferEntry[0])));
      };

  @OpenApi(
      summary = "Find a specific transfer entry based on its ID",
      description = "Find a specific transfer entry based on its ID",
      tags = {"Transfer"},
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TransferEntry.class)},
              description = "Returns the requested transfer entry"),
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
              description = "Requested ID does not exist"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(
      path = "api/transfer/{id}",
      method = "GET",
      roles = {
          Route.RestRoles.ANONYMOUS,
          Route.RestRoles.USER,
          Route.RestRoles.SERVER,
          Route.RestRoles.DEV
      })
  public static Handler getID =
      ctx -> {
        try {
          long id = Long.parseLong(ctx.pathParam("id"));
          if (id < 0) {
            ctx.status(400)
                .result(response("Invalid ID Path",
                    "ID must be a number greater or equal to 0"));
            return;
          }
          TransferEntry entry = SQLCacheTransfers.getID(id);
          if (entry != null) {
            ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, entry)));
          } else {
            ctx.status(404)
                .result(response("Not Found",
                    "Transfer ID with the provided ID does not exist"));
          }
        } catch (NumberFormatException e) {
          ctx.status(400)
              .result(response("Invalid ID Path",
                  "ID must be a number greater or equal to 0"));
        }
      };

  @OpenApi(
      summary = "Find a specific transfer entry based on its ID and the requested data",
      description = "Find a specific transfer entry based on its ID and the requested data",
      tags = {"Transfer"},
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TransferEntry.class)},
              description = "Returns the requested transfer entry"),
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
              description = "Requested ID does not exist"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(
      path = "api/transfer/{id}/{data}",
      method = "GET",
      roles = {
          Route.RestRoles.ANONYMOUS,
          Route.RestRoles.USER,
          Route.RestRoles.SERVER,
          Route.RestRoles.DEV
      })
  public static Handler getData =
      ctx -> {
        try {
          long id = Long.parseLong(ctx.pathParam("id"));
          if (id < 0) {
            ctx.status(400)
                .result(response("Invalid ID Path",
                    "ID must be a number greater or equal to 0"));
            return;
          }
          String field = convertPathToField(ctx.pathParam("data"));
          if (field == null) {
            ctx.status(400).result(response("Invalid Data Path", "Invalid Data"));
            return;
          }
          TransferEntry entry = SQLCacheTransfers.getID(id);
          if (entry != null) {
            Field dataField = entry.getClass().getDeclaredField(field);
            ctx.status(200)
                .result(GSON.toJson(wipeAllExceptField(entry.clone(), dataField)));
          } else {
            ctx.status(404)
                .result(response("Not Found",
                    "Transfer ID with the provided ID does not exist"));
          }
        } catch (NumberFormatException e) {
          ctx.status(400)
              .result(response("Invalid ID Path",
                  "ID must be a number greater or equal to 0"));
        }
      };

  @OpenApi(
      summary = "Update / Override an existing transfer entry",
      description = "Update / Override an existing transfer entry",
      tags = {"Transfer"},
      headers = {
          @OpenApiParam(
              name = "Authorization",
              description = "Authorization Token to be used for authentication within the rest api")
      },
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TransferEntry.class)},
              description = "Updated transfer entry"),
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
              description = "Requested ID does not exist"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(
      path = "api/transfer/{id}",
      method = "PUT",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler update =
      ctx -> {
        try {
          TransferEntry updateEntry = GSON.fromJson(ctx.body(), TransferEntry.class);
          if (isValidTransferEntry(ctx, updateEntry)) {
            try {
              long id = Long.parseLong(ctx.pathParam("id"));
              if (id != updateEntry.transfer_id) {
                ctx.status(400)
                    .result(response("ID Mismatch", "ID in Body and Path dont match"));
                return;
              }
              SQLCacheTransfers.update(updateEntry, new String[]{"items"});
              ctx.status(200).result(GSON.toJson(SQLCacheTransfers.getID(id)));
            } catch (NumberFormatException e) {
              ctx.status(400)
                  .result(
                      response("Invalid Path",
                          "Path ID must be a number greater or equal to 0"));
            }
          }
        } catch (JsonParseException e) {
          ctx.status(400)
              .result(response("Invalid JSON", "Cannot Parse body into Transfer Entry"));
        }
      };

  @OpenApi(
      summary = "Update the specified entry in the transfer entry",
      description = "Update the specified entry in the transfer entry",
      tags = {"Transfer"},
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TransferEntry.class)},
              description = "Updated transfer entry"),
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
              description = "Requested ID does not exist"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(
      path = "api/transfer/{id}/{data}",
      method = "PATCH",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler patch =
      ctx -> {
        try {
          long id = Long.parseLong(ctx.pathParam("id"));
          if (id < 0) {
            ctx.status(400)
                .result(response("Invalid ID Path",
                    "ID must be a number greater or equal to 0"));
            return;
          }
          String field = convertPathToField(ctx.pathParam("data"));
          if (field == null) {
            ctx.status(400).result(response("Invalid Data Path", "Invalid Data"));
            return;
          }
          TransferEntry entry = SQLCacheTransfers.getID(id);
          if (entry != null) {
            SQLCacheTransfers.update(entry, new String[]{field});
            ctx.status(200).result(GSON.toJson(SQLCacheTransfers.getID(id)));
          } else {
            ctx.status(404)
                .result(response("Not Found",
                    "Transfer ID with the provided ID does not exist"));
          }
        } catch (NumberFormatException e) {
          ctx.status(400)
              .result(response("Invalid ID Path",
                  "ID must be a number greater or equal to 0"));
        }
      };

  @OpenApi(
      summary = "Delete the specified transfer entry",
      description = "Delete a specific transfer entry",
      tags = {"Transfer"},
      responses = {
          @OpenApiResponse(
              status = "200",
              content = {@OpenApiContent(from = TransferEntry.class)},
              description = "Transfer Entry that was deleted"),
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
              description = "Requested ID does not exist"),
          @OpenApiResponse(
              status = "500",
              content = {@OpenApiContent(from = MessageResponse.class)},
              description =
                  "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(
      path = "api/transfer/{id}",
      method = "DELETE",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler delete =
      ctx -> {
        try {
          long id = Long.parseLong(ctx.pathParam("id"));
          TransferEntry entry = SQLCacheTransfers.getID(id);
          if (entry != null) {
            SQLCacheTransfers.delete(id);
            ctx.status(200).result(GSON.toJson(entry));
          } else {
            ctx.status(404)
                .result(
                    response("Not Found",
                        "Transfer Entry with the provided ID does not exist"));
          }
        } catch (NumberFormatException e) {
          ctx.status(400)
              .result(response("Invalid Path",
                  "Path ID must be a number greater or equal to 0"));
        }
      };

  /**
   * Validates a transfer entry
   *
   * @param ctx context of the message
   * @param entry entry to be checked, if its valid
   * @return validates the provided transfer entry, if its valid or not
   */
  public static boolean isValidTransferEntry(Context ctx, TransferEntry entry) {
    List<MessageResponse> errors = new ArrayList<>();
    // Validate UUID
    if (entry.uuid == null || entry.uuid.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid UUID", "UUid must be a valid UUID"));
    }
    try {
      if (entry.uuid != null) {
        UUID.fromString(entry.uuid);
      }
    } catch (Exception e) {
      errors.add(new MessageResponse("Invalid UUID", "UUid must be a valid UUID"));
    }
    // Validate Items
    if (entry.items == null || entry.items.length == 0) {
      errors.add(
          new MessageResponse("Invalid Items",
              "Item's must be valid and have a count above 0"));
    }
    // Validate ServerID
    if (entry.server_id == null || entry.server_id.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid ServerID", "ServerID must be non-null"));
    }
    if (errors.size() == 0) {
      return true;
    }
    ctx.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
    return false;
  }

  /**
   * Removes the data, the given rank does not have access to
   *
   * @param ctx context of the message
   * @param entry instance to remove the values from
   * @return instance with certain values removed / null'd
   */
  private static TransferEntry filterBasedOnPerms(Context ctx, TransferEntry entry) {
    Route.RestRoles role = EndpointSecurity.getRole(ctx);
    if (role.equals(Route.RestRoles.DEV) || role.equals(Route.RestRoles.SERVER)) {
      return entry;
    }
    TransferEntry clone = entry.clone();
    if (role.equals(Route.RestRoles.USER)) {
      // TODO Based on SystemPerms
    }
    clone.items = null;
    return clone;
  }

  /**
   * Converts a path param into its name for use as a field / lookup
   *
   * @param data path param to be converted
   * @return Converts a path param into its field name, in Rank
   * @see Rank
   */
  public static String convertPathToField(String data) {
    if (data.equalsIgnoreCase("transfer-id") || data.equalsIgnoreCase("id")) {
      return "transfer_id";
    }
    if (data.equalsIgnoreCase("uuid")) {
      return "uuid";
    }
    if (data.equalsIgnoreCase("start-time")) {
      return "start_time";
    }
    if (data.equalsIgnoreCase("items")) {
      return "items";
    }
    if (data.equalsIgnoreCase("server-id")) {
      return "server_id";
    }
    return null;
  }

  /**
   * Generates a SQL Statement for get users with filters applied
   *
   * @param ctx context to get the information from the user
   * @return sql statement for currency lookup
   */
  private static String createSQLForTransferEntryWithFilters(Context ctx) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT * FROM " + SQLCacheTransfers.TRANSFERS_TABLE + " WHERE ");
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
    // Verify, Check and Apply serverID Filter
    String startTime = ctx.queryParam("start-time");
    if (startTime != null && !startTime.trim().isEmpty()) {
      sqlBuilder.append("startTime > ").append(startTime).append(" AND ");
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
