/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.routes.data;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

import com.google.gson.JsonParseException;
import io.javalin.core.validation.Validator;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RankRoutes {

  @OpenApi(
      summary = "Creates a new rank with the provided information",
      description = "Creates a new rank with the provided information",
      tags = {"Rank"},
      headers = {
        @OpenApiParam(
            name = "Authorization",
            description = "Authorization Token to used for authentication within the rest API",
            required = true)
      },
      requestBody =
          @OpenApiRequestBody(
              content = {@OpenApiContent(from = Account.class)},
              required = true,
              description = "Rank information used to create the requested rank"),
      responses = {
        @OpenApiResponse(
            status = "201",
            content = {@OpenApiContent(from = Rank.class)},
            description = "Rank has been created successfully, rankID is also returned"),
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
      path = "api/rank",
      method = "POST",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler createRank =
      ctx -> {
        try {
          Rank newRank = GSON.fromJson(ctx.body(), Rank.class);
          if (isValidRank(ctx, newRank)) {
            // Check for existing rank
            Rank rank = SQLCacheRank.get(newRank.name);
            if (rank == null) {
              rank = SQLCacheRank.create(newRank);
              if (rank == null) {
                ctx.status(500)
                    .result(response("Rank Failed to Create", "Rank has failed to be created!"));
                return;
              }
              ctx.status(201).result(GSON.toJson(filterBasedOnPerms(ctx, rank)));
            } else { // Rank exists
              ctx.status(409)
                  .result(response("Rank Exists", "Rank '" + rank.name + "' already exists"));
            }
          }
        } catch (JsonParseException e) {
          ctx.status(422).result(response("Invalid JSON", "Failed to parse the body into an Rank"));
        }
      };

  @OpenApi(
      summary = "Overrides the given rank information with the provided information",
      description = "Override a rank with the provided information",
      tags = {"Rank"},
      pathParams = {
        @OpenApiParam(name = "name", description = "Name of the a given rank", required = true)
      },
      headers = {
        @OpenApiParam(
            name = "Authorization",
            description = "Authorization Token to used for authentication within the rest API",
            required = true)
      },
      requestBody =
          @OpenApiRequestBody(
              content = {@OpenApiContent(from = Rank.class)},
              required = true,
              description = "Rank information used to update the requested account"),
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = Rank.class)},
            description = "Rank has been updated successfully"),
        @OpenApiResponse(
            status = "400",
            content = {@OpenApiContent(from = MessageResponse.class)},
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
            description = "Rank does not exist"),
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
      path = "api/rank/{name}",
      method = "PUT",
      roles = {Route.RestRoles.DEV})
  public static Handler overrideRank =
      ctx -> {
        String name = ctx.pathParam("name");
        if (name != null && !name.trim().isEmpty() && name.matches("[A-Za-z0-9]+")) {
          try {
            // Check for valid json
            Rank rank = GSON.fromJson(ctx.body(), Rank.class);
            if (rank.name.equalsIgnoreCase(name)) {
              if (isValidRank(ctx, rank)) {
                // Update / Override Rank
                if (SQLCacheRank.update(rank, SQLCacheRank.getColumns()))
                  ctx.status(200).result(GSON.toJson(SQLCacheRank.get(rank.name)));
                else
                  ctx.status(500)
                      .result(response("Rank Failed To Update", "Rank Update has failed!"));
              }
            } else
              ctx.status(400)
                  .result(
                      response(
                          "Bad Request",
                          "Names's don't match, path: '"
                              + name
                              + "' and body: '"
                              + rank.name
                              + "'"));
          } catch (Exception e) {
            ctx.status(422)
                .result(response("Invalid JSON", "Failed to parse the body into an Rank"));
          }
        } else ctx.status(400).result(response("Bad Request", "Name is not valid"));
      };

  @OpenApi(
      summary = "Overrides the given rank information with the provided information",
      description = "Override a rank with the provided information",
      tags = {"Rank"},
      pathParams = {
        @OpenApiParam(name = "name", description = "Name of the a given account", required = true),
        @OpenApiParam(
            name = "data",
            description = "Information to be patched / updated",
            required = true),
      },
      headers = {
        @OpenApiParam(
            name = "Authorization",
            description = "Authorization Token to used for authentication within the rest API",
            required = true)
      },
      requestBody =
          @OpenApiRequestBody(
              content = {@OpenApiContent(from = Rank.class)},
              required = true,
              description = "Rank information used to update the requested account"),
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = Rank.class)},
            description = "Rank has been updated successfully,"),
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
            description = "Rank does not exist"),
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
      path = "api/rank/{name}/{data}",
      method = "PATCH",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler patchRank =
      ctx -> {
        String name = ctx.pathParam("name");
        if (name != null && !name.trim().isEmpty() && name.matches("[A-Za-z0-9]+")) {
          Rank rank = SQLCacheRank.get(name);
          if (rank != null) {
            String fieldName = convertPathToField(ctx.pathParam("data"));
            if (fieldName != null) {
              try {
                Rank inputData = GSON.fromJson(ctx.body(), Rank.class);
                Field field = rank.getClass().getDeclaredField(fieldName);
                field.set(rank, field.get(inputData));
                if (isValidRank(ctx, rank)) {
                  SQLCacheRank.update(rank, new String[] {fieldName});
                  ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, rank)));
                }
              } catch (JsonParseException e) {
                ctx.status(422)
                    .result(response("Invalid JSON", "Failed to parse the body into an Rank"));
              }
            } else
              ctx.status(400)
                  .result(
                      response(
                          "Bad Request",
                          ctx.pathParam("data") + " is not valid entry for the requested Rank"));
          } else
            ctx.status(404)
                .result(response("Rank Not Found", "Rank '" + name + "' does not exist"));
        } else ctx.status(400).result(response("Bad Request", "Name is not valid"));
      };

  @OpenApi(
      summary = "Get a ranks information via name",
      description = "Gets a ranks information via name",
      tags = {"Rank"},
      pathParams = {
        @OpenApiParam(name = "name", description = "Name of the a given rank", required = true)
      },
      headers = {
        @OpenApiParam(
            name = "Authorization",
            description = "Authorization Token to used for authentication within the rest API",
            required = true)
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = Rank.class)},
            description = "Rank is returned"),
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
            description = "Rank does not exist"),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = MessageResponse.class)},
            description =
                "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(path = "api/rank/{name}", method = "GET")
  public static Handler getRank =
      ctx -> {
        String name = ctx.pathParam("name");
        if (name != null && !name.trim().isEmpty() && name.matches("[A-Za-z0-9]+")) {
          Rank rank = SQLCacheRank.get(name);
          if (rank != null) ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, rank)));
          else
            ctx.status(404)
                .result(
                    response("Rank Not Found", "Rank with the name '" + name + "' does not exist"));
        } else {
          ctx.status(400).result(response("Bad Request", "Name is not valid"));
        }
      };

  @OpenApi(
      summary = "Get a specific data entry for the given rank via name",
      description = "Get a specific entry for the given rank via name",
      tags = {"Rank"},
      pathParams = {
        @OpenApiParam(name = "name", description = "Name of the a given rank", required = true),
        @OpenApiParam(
            name = "data",
            description = "Information to be patched / updated",
            required = true),
      },
      headers = {
        @OpenApiParam(
            name = "Authorization",
            description = "Authorization Token to used for authentication within the rest API",
            required = true)
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = Account.class)},
            description = "Requested Rank information is returned"),
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
            description = "Rank does not exist"),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = MessageResponse.class)},
            description =
                "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(path = "api/rank/{name}/{data}", method = "GET")
  public static Handler getRankInfo =
      ctx -> {
        String name = ctx.pathParam("name");
        if (name != null && !name.trim().isEmpty() && name.matches("[A-Za-z0-9]+")) {
          String pathParam = ctx.pathParam("data");
          String field = convertPathToField(pathParam);
          if (field != null) {
            Rank rank = SQLCacheRank.get(name);
            if (rank != null) {
              Field rankField = rank.getClass().getDeclaredField(field);
              rank = wipeAllExceptField(rank, rankField);
              ctx.status(200).result(GSON.toJson(rank));
            } else
              ctx.status(404)
                  .result(
                      response(
                          "Rank Not Found", "Rank with the name '" + name + "' does not exist"));
          } else ctx.status(400).result(response("Bad Request", "Invalid data field"));
        } else ctx.status(400).result(response("Bad Request", "Name is not valid"));
      };

  @OpenApi(
      summary = "Get a list of all Ranks",
      description =
          "Get a list of all ranks, query filtering is enabled, Max amount per request is set on auth token permissions",
      tags = {"Rank"},
      queryParams = {
        @OpenApiParam(name = "name", description = "Filter based on name, full or partial"),
        @OpenApiParam(
            name = "permission",
            description = "Filter based on perms, single or multiple nodes"),
        @OpenApiParam(name = "inheritance", description = "Filter based on inheritances"),
        @OpenApiParam(name = "prefix", description = "Filter based on prefix"),
        @OpenApiParam(name = "prefixPriority", description = "Filter based on prefix priority"),
        @OpenApiParam(name = "suffix", description = "Filter based on suffix priority"),
        @OpenApiParam(name = "suffixPriority", description = "Filter based on suffix priority"),
        @OpenApiParam(name = "color", description = "Filter based on color"),
        @OpenApiParam(name = "colorPriority", description = "Filter based on color priority"),
      },
      headers = {
        @OpenApiParam(
            name = "Authorization",
            description = "Authorization Token to used for authentication within the rest API",
            required = true)
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = Rank[].class)},
            description = "Rank has been returned successfully,"),
        @OpenApiResponse(
            status = "400",
            content = {@OpenApiContent(from = MessageResponse.class)},
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
            status = "500",
            content = {@OpenApiContent(from = MessageResponse.class)},
            description =
                "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(path = "api/rank", method = "GET")
  public static Handler getRanks =
      ctx -> {
        String sql = createSQLForRanksWithFilters(ctx);
        // Send Request and Process
        List<Rank> ranks = SQLDirect.queryArray(sql, new Rank());
        List<Rank> permedRanks = new ArrayList<>();
        for (Rank rank : ranks) permedRanks.add(filterBasedOnPerms(ctx, rank));
        ctx.status(200).result(GSON.toJson(permedRanks.toArray(new Rank[0])));
      };

  @OpenApi(
      summary = "Delete a rank via name",
      description = "Delete a rank via name",
      tags = {"Rank"},
      pathParams = {
        @OpenApiParam(name = "name", description = "name of the a given Rank", required = true)
      },
      headers = {
        @OpenApiParam(
            name = "Authorization",
            description = "Authorization Token to used for authentication within the rest API",
            required = true)
      },
      responses = {
        @OpenApiResponse(
            status = "200",
            content = {@OpenApiContent(from = Rank.class)},
            description = "Deleted Rank is returned"),
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
            description = "Rank does not exist"),
        @OpenApiResponse(
            status = "500",
            content = {@OpenApiContent(from = MessageResponse.class)},
            description =
                "The server has encountered an error, please contact the server's admin to check the logs")
      })
  @Route(
      path = "api/rank/{name}",
      method = "DELETE",
      roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
  public static Handler deleteRank =
      ctx -> {
        String name = ctx.pathParam("name");
        if (name != null && !name.trim().isEmpty() && name.matches("[A-Za-z0-9]+")) {
          Rank rank = SQLCacheRank.get(name);
          if (rank != null) {
            boolean deleted = SQLCacheRank.delete(rank.name);
            if (deleted) ctx.status(200).result(GSON.toJson(rank));
            else
              ctx.status(500)
                  .result(
                      response(
                          "Rank Not Deleted", "Rank '" + rank.name + "' failed to be deleted!"));
          } else
            ctx.status(404)
                .result(
                    response("Rank Not Found", "Rank with the name '" + name + "' does not exist"));
        } else ctx.status(400).result(response("Bad Request", "Name is not valid"));
      };

  /**
   * Validates if the instance is a valid rank or not
   *
   * @param rank instance of the rank to verify
   * @return if the rank is valid, invalid will result in messages being sent on the context
   */
  private static boolean isValidRank(Context ctx, Rank rank) {
    List<MessageResponse> errors = new ArrayList<>();
    // Name
    if (rank.name.trim().isEmpty() || !rank.name.matches("[A-Za-z0-9]+"))
      errors.add(
          new MessageResponse(
              "Bad Request", "Name must be alpha-numeric with a size of 1 or greater"));
    // Prefix
    if (!rank.prefix.trim().isEmpty() && !rank.prefix.matches("[A-Za-z0-9&_()*\\[\\]]+"))
      errors.add(new MessageResponse("Bad Request", "Prefix must be alpha-numeric / &_()[] or *"));
    // Suffix
    if (!rank.suffix.trim().isEmpty() && !rank.suffix.matches("[A-Za-z0-9&_()*\\[\\]]+"))
      errors.add(new MessageResponse("Bad Request", "Suffix must be alpha-numeric / &_() or *"));
    if (errors.size() > 0) {
      ctx.status(400).result(GSON.toJson(errors));
      return false;
    }
    return true;
  }

  /**
   * Removes the data, the given rank does not have access to
   *
   * @param ctx context of the message
   * @param rank
   * @return instance with certain values removed / null'd
   */
  private static Rank filterBasedOnPerms(Context ctx, Rank rank) {
    Route.RestRoles role = EndpointSecurity.getRole(ctx);
    if (role.equals(Route.RestRoles.DEV) || role.equals(Route.RestRoles.SERVER)) return rank;
    Rank clone = rank.clone();
    if (role.equals(Route.RestRoles.USER)) {
      // TODO Based on SystemPerms
    }
//    clone.name = null;
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
    if (data.equalsIgnoreCase("name"))
      return "name";
    else if (data.equalsIgnoreCase("permissions")
        || data.equalsIgnoreCase("perms")
        || data.equalsIgnoreCase("permission")) return "permissions";
    else if (data.equalsIgnoreCase("inheritance") || data.equalsIgnoreCase("inheritances"))
      return "inheritance";
    else if (data.equalsIgnoreCase("prefix")) return "prefix";
    else if (data.equalsIgnoreCase("prefix-priority") || data.equalsIgnoreCase("prefixPriority"))
      return "prefix_priority";
    else if (data.equalsIgnoreCase("suffix")) return "suffix";
    else if (data.equalsIgnoreCase("suffix-priority") || data.equalsIgnoreCase("suffixPriority"))
      return "suffix_priority";
    else if (data.equalsIgnoreCase("color")) return "color";
    else if (data.equalsIgnoreCase("color-priority") || data.equalsIgnoreCase("colorPriority"))
      return "color_priority";
    return null;
  }

  /**
   * Removes all the fields from this object, except for the one specified
   *
   * @param rank instance to remove / copy the values from
   * @param safe the field that will be copied over
   * @return Rank will all but one field removed, set to null
   * @throws IllegalAccessException This should never happen, unless Rank has been modified
   */
  private static Rank wipeAllExceptField(Rank rank, Field safe) throws IllegalAccessException {
    rank = rank.clone();
    for (Field field : rank.getClass().getDeclaredFields())
      if (!field.equals(safe)) field.set(rank, null);
    if (safe.get(rank) instanceof String && ((String) safe.get(rank)).isEmpty()) safe.set(rank, "");
    return rank;
  }

  /**
   * Generates a SQL Statement for getting ranks with filters applied
   *
   * @param ctx context to get the information from the request
   * @return sql statement for rank lookup
   */
  private static String createSQLForRanksWithFilters(Context ctx) {
    StringBuilder sqlBuilder = new StringBuilder();
    sqlBuilder.append("SELECT * FROM " + SQLCacheRank.RANKS_TABLE);
    StringBuilder whereBuilder = new StringBuilder();
    whereBuilder.append(" WHERE ");
    String name = ctx.queryParam("name");
    if (name != null && !name.trim().isEmpty())
      whereBuilder.append("name LIKE '%").append(name).append("%' AND");
    String perms = ctx.queryParam("permission");
    if (perms != null && !perms.trim().isEmpty())
      whereBuilder.append("permission LIKE '%").append(perms).append("%' AND");
    String inheritance = ctx.queryParam("inheritance");
    if (inheritance != null && !inheritance.trim().isEmpty())
      whereBuilder.append("inheritance LIKE '%").append(inheritance).append("%' AND");
    String prefix = ctx.queryParam("prefix");
    if (prefix != null && !prefix.trim().isEmpty())
      whereBuilder.append("prefix LIKE '%").append(prefix).append("%' AND");
    Validator<Integer> prefixPriority = ctx.queryParamAsClass("prefix-priority", Integer.class);
    if (ctx.queryParam("prefix-priority") != null
        && !ctx.queryParam("prefix-priority").trim().isEmpty()
        && prefixPriority.errors().isEmpty())
      whereBuilder.append("prefix_priority='").append(prefixPriority.get()).append("' AND");
    String suffix = ctx.queryParam("suffix");
    if (suffix != null && !suffix.trim().isEmpty())
      whereBuilder.append("suffix LIKE '%").append(suffix).append("%' AND");
    Validator<Integer> suffixPriority = ctx.queryParamAsClass("suffix-priority", Integer.class);
    if (ctx.queryParam("suffix-priority") != null
        && !ctx.queryParam("suffix-priority").trim().isEmpty()
        && suffixPriority.errors().isEmpty())
      whereBuilder.append("suffix_priority='").append(suffixPriority.get()).append("' AND");
    String color = ctx.queryParam("color");
    if (color != null && !color.trim().isEmpty())
      whereBuilder.append("color LIKE '%").append(color).append("%' AND");
    Validator<Integer> colorPriority = ctx.queryParamAsClass("color-priority", Integer.class);
    if (ctx.queryParam("color-priority") != null
        && !ctx.queryParam("color-priority").trim().isEmpty()
        && colorPriority.errors().isEmpty())
      whereBuilder.append("color_priority='").append(colorPriority.get()).append("' AND");
    String sql = sqlBuilder.toString();
    String whereSQL = whereBuilder.toString();
    if (whereSQL.endsWith("AND")) {
      whereSQL = whereSQL.substring(0, whereSQL.length() - 3);
      sql = sql + whereSQL;
    }
    return sql;
  }
}
