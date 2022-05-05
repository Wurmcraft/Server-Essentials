package io.wurmatron.serveressentials.routes.data;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Action;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLActions;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;

import java.util.*;

import java.util.ArrayList;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class ActionRoutes {

  private static final String RELATED_ID = "related-id";
  private static final String HOST = "host";
  private static final String ACTION = "action";
  private static final String ACTION_DATA = "action_data";
  private static final String TIMESTAMP = "timestamp";

  @OpenApi(
      summary = "Create / log a new action",
      description = "Create / log a new action",
      tags = {"Action"},
      requestBody = @OpenApiRequestBody(content = {
          @OpenApiContent(from = Action.class)}, required = true, description = "Information about the action"),
      responses = {
          @OpenApiResponse(status = "201", content = {
              @OpenApiContent(from = Action.class)}, description = "Action instance of to created / logged"),
          @OpenApiResponse(status = "400", content = {
              @OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
          @OpenApiResponse(status = "401", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
          @OpenApiResponse(status = "403", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
          @OpenApiResponse(status = "409", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Action already exists"),
          @OpenApiResponse(status = "422", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
          @OpenApiResponse(status = "500", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
      }
  )
  @Route(path = "api/action", method = "POST", roles = {Route.RestRoles.USER,
      Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler create = ctx -> {
    try {
      // TODO Check for existing action
      Action newAction = GSON.fromJson(ctx.body(), Action.class);
      if (isValidAction(ctx, newAction)) {
        ctx.status(201).result(GSON.toJson(SQLActions.create(newAction)));
      } else {
        ctx.status(400);
      }
    } catch (JsonParseException e) {
      ctx.status(422).result(
          response("Invalid JSON", "Failed to parse the body into an Action Entry"));
    }
  };

  @OpenApi(
      summary = "Get a list / array of action's matching the requested criteria, (query)",
      description = "Get a list / array of action's matching the requested criteria, (query)",
      tags = {"Action"},
      queryParams = {
          @OpenApiParam(name = "related-id", description = "Server ID or discord ID related to the provided action"),
          @OpenApiParam(name = "host", description = "Type that the related-id is related to, 'Minecraft', 'Discord'"),
          @OpenApiParam(name = "action", description = "Type of action that occurred"),
          @OpenApiParam(name = "min-timestamp", description = "Starting Time / Earliest time this action can have occurred"),
          @OpenApiParam(name = "max-timestamp", description = "Last time this action can has occurred"),
      },
      responses = {
          @OpenApiResponse(status = "200", content = {
              @OpenApiContent(from = Action[].class)}, description = "List of actions based on your requested filters (query)"),
          @OpenApiResponse(status = "400", content = {
              @OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
          @OpenApiResponse(status = "401", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
          @OpenApiResponse(status = "403", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
          @OpenApiResponse(status = "404", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Action does not exist"),
          @OpenApiResponse(status = "422", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
          @OpenApiResponse(status = "500", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
      }
  )
  @Route(path = "api/action", method = "GET")
  public static Handler get = ctx -> {
    String min = ctx.queryParam("min-timestamp");
    String max = ctx.queryParam("max-timestamp");
    if (min != null && !isNumber(min)) {
      ctx.status(400)
          .result(response("Bad Request", "min-timestamp must be a valid timestamp"));
      return;
    }
    if (max != null && !isNumber(max)) {
      ctx.status(400)
          .result(response("Bad Request", "max-timestamp must be a valid timestamp"));
      return;
    }
    String sql = createSQLForActionsWithFilters(ctx);
    List<Action> actions = SQLDirect.queryArray(sql, new Action());
    ctx.status(200).result(GSON.toJson(actions.toArray(new Action[0])));
  };

  @OpenApi(
      summary = "Update an existing action",
      description = "Update an existing action",
      tags = {"Action"},
      requestBody = @OpenApiRequestBody(content = {
          @OpenApiContent(from = Action.class)}, required = true, description = "Information about the action"),
      responses = {
          @OpenApiResponse(status = "200", content = {
              @OpenApiContent(from = Action.class)}, description = "Updated action instance is returned"),
          @OpenApiResponse(status = "400", content = {
              @OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
          @OpenApiResponse(status = "401", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
          @OpenApiResponse(status = "403", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
          @OpenApiResponse(status = "404", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Action does not exist"),
          @OpenApiResponse(status = "422", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
          @OpenApiResponse(status = "500", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
      }
  )
  @Route(path = "api/action", method = "PUT")
  public static Handler update = ctx -> {
    try {
      Action updatedAction = GSON.fromJson(ctx.body(), Action.class);
      // Check for Action to update
      Action sqlAction = null;
      List<Action> sqlActions = SQLActions.get(updatedAction.host, updatedAction.action,
          updatedAction.related_id);
      for (Action action : sqlActions) {
        if (action.timestamp.equals(updatedAction.timestamp)) {
          sqlAction = action;
          break;
        }
      }
      if (sqlAction != null) {
        Action action = SQLActions.update(updatedAction, new String[]{ACTION_DATA});
        ctx.status(200).result(GSON.toJson(action));
      } else {
        ctx.status(404)
            .result(response("Invalid Action", "Requested Action does not exist"));
      }
    } catch (JsonParseException e) {
      ctx.status(422).result(
          response("Invalid JSON", "Failed to parse the body into an Action Entry"));
    }
  };

  @OpenApi(
      summary = "Delete an existing action",
      description = "Delete an existing action",
      tags = {"Action"},
      requestBody = @OpenApiRequestBody(content = {
          @OpenApiContent(from = Action.class)}, required = true, description = "Information about the action"),
      responses = {
          @OpenApiResponse(status = "200", content = {
              @OpenApiContent(from = Action.class)}, description = "Deleted action is returned"),
          @OpenApiResponse(status = "400", content = {
              @OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
          @OpenApiResponse(status = "401", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
          @OpenApiResponse(status = "403", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
          @OpenApiResponse(status = "404", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Action does not exist"),
          @OpenApiResponse(status = "422", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
          @OpenApiResponse(status = "500", content = {
              @OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
      }
  )
  @Route(path = "api/action", method = "DELETE")
  public static Handler delete = ctx -> {
    try {
      Action updatedAction = GSON.fromJson(ctx.body(), Action.class);
      // Check for Action to delete
      Action sqlAction = null;
      List<Action> sqlActions = SQLActions.get(updatedAction.host, updatedAction.action,
          updatedAction.related_id);
      for (Action action : sqlActions) {
        if (action.timestamp.equals(updatedAction.timestamp)) {
          sqlAction = action;
          break;
        }
      }
      if (sqlAction != null) {
        Action action = SQLActions.delete(updatedAction.host, updatedAction.action,
            sqlAction.related_id, sqlAction.timestamp);
        ctx.status(200).result(GSON.toJson(action));
      } else {
        ctx.status(404)
            .result(response("Invalid Action", "Requested Action does not exist"));
      }
    } catch (JsonParseException e) {
      ctx.status(422).result(
          response("Invalid JSON", "Failed to parse the body into an Action Entry"));
    }
  };

  /**
   * Checks if the provided action is valid
   *
   * @param context message context for the request
   * @param action action to check if its valid
   * @return if the provided action is valid
   */
  public static boolean isValidAction(Context context, Action action) {
    List<MessageResponse> errors = new ArrayList<>();
    // Validate relatedID
    if (action.related_id.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid Entry", "Invalid / Empty RelatedID"));
    }
    // Validate host
    if (action.host.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid Entry", "Invalid / Empty Host"));
    }
    if (action.action.trim().isEmpty()) {
      errors.add(new MessageResponse("Invalid Entry", "Invalid / Empty Action"));
    }
    if (action.timestamp.length() <= 0) {
      errors.add(
          new MessageResponse("Invalid Entry", "Timestamp must be greater than 0"));
    }
    if (errors.size() > 0) {
      context.status(400).result(GSON.toJson(errors));
      return false;
    }
    return true;
  }

  /**
   * Collects the filters from the message context and create a sql statement with the
   * provided information
   *
   * @param ctx context for the requested sql statement
   * @return sql statement matching the requested filters
   */
  private static String createSQLForActionsWithFilters(Context ctx) {
    StringBuilder builder = new StringBuilder();
    builder.append("SELECT * FROM " + SQLActions.ACTIONS_TABLE + " WHERE ");
    String relatedID = ctx.queryParam("related-id");
    if (relatedID != null && !relatedID.trim().isEmpty()) {
      builder.append(RELATED_ID + " LIKE '" + relatedID + "%' AND ");
    }
    String host = ctx.queryParam("host");
    if (host != null && !host.trim().isEmpty()) {
      builder.append(HOST  + " LIKE '" + host + "%' AND ");
    }
    String action = ctx.queryParam("action");
    if (action != null && !action.trim().isEmpty()) {
      builder.append(ACTION + " LIKE '" + action + "%' AND ");
    }
    String min = ctx.queryParam("min-timestamp");
    String max = ctx.queryParam("max-timestamp");
    if (min != null && max != null && !min.trim().isEmpty() && !max.trim().isEmpty()) {
      builder.append(TIMESTAMP + " BETWEEN " + min + " AND " + max);
    } else if (min != null && !min.trim().isEmpty()) {
      builder.append(TIMESTAMP + " <= " + min);
    } else if (max != null && !max.trim().isEmpty()) {
      builder.append(TIMESTAMP + " >= " + max);
    }
    String sql = builder.toString();
    int shift = 4;
    if (sql.endsWith("WHERE ")) {
      shift = 6;
    }
    sql = sql.substring(0, sql.length() - shift);
    sql = sql + ";";
    return sql;
  }

  /**
   * Checks if the provided string is a positive number
   *
   * @param num string of a possible number
   * @return if the string is a valid number, equal or greater than 0
   */
  public static boolean isNumber(String num) {
    if (num != null && !num.trim().isEmpty()) {
      try {
        long no = Long.parseLong(num);
        if (no >= 0) {
          return true;
        }
      } catch (NumberFormatException e) {
      }
    }
    return false;
  }
}
