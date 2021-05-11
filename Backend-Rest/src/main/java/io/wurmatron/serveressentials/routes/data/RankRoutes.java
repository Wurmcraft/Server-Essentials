package io.wurmatron.serveressentials.routes.data;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class RankRoutes {

    @OpenApi(
            summary = "Creates a new rank with the provided information",
            description = "Creates a new rank with the provided information",
            tags = {"Rank"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Account.class)}, required = true, description = "Rank information used to create the requested rank"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Rank.class)}, description = "Rank has been created successfully, rankID is also returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "409", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Rank already exists"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler createRank = ctx -> {
        try {
            Rank newRank = GSON.fromJson(ctx.body(), Rank.class);
            if (isValidRank(ctx, newRank)) {
                // Check for existing rank
                Rank rank = SQLCacheRank.get(newRank.name);
                if (rank == null) {
                    rank = SQLCacheRank.create(newRank);
                    if (rank == null) {
                        ctx.status(500).result(response("Rank Failed to Create", "Rank has failed to be created!"));
                        return;
                    }
                    ctx.status(201).result(GSON.toJson(filterBasedOnPerms(ctx, rank)));
                } else {    // Rank exists
                    ctx.status(409).result(response("Rank Exists", "Rank '" + rank.name + "' already exists"));
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
            pathParams = {@OpenApiParam(name = "name", description = "Name of the a given rank", required = true)},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Rank.class)}, required = true, description = "Rank information used to update the requested account"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Rank.class)}, description = "Rank has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse.class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Rank does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank/:name", method = "PUT", roles = {Route.RestRoles.DEV})
    public static Handler overrideRank = ctx -> {

    };

    @OpenApi(
            summary = "Overrides the given rank information with the provided information",
            description = "Override a rank with the provided information",
            tags = {"Rank"},
            pathParams = {
                    @OpenApiParam(name = "name", description = "Name of the a given account", required = true),
                    @OpenApiParam(name = "data", description = "Information to be patched / updated", required = true),
            },
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Rank.class)}, required = true, description = "Rank information used to update the requested account"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Rank.class)}, description = "Rank has been updated successfully,"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Rank does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank/:name/:data", method = "PATCH", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler patchRank = ctx -> {

    };

    @OpenApi(
            summary = "Get a ranks information via name",
            description = "Gets a ranks information via name",
            tags = {"Rank"},
            pathParams = {@OpenApiParam(name = "name", description = "Name of the a given rank", required = true)},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Rank.class)}, description = "Rank is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Rank does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank/:name", method = "GET")
    public static Handler getRank = ctx -> {
        String name = ctx.pathParam("name", String.class).get();
        if (name != null && !name.trim().isEmpty() && !name.matches("[A-Za-z0-9]+")) {
            Rank rank = SQLCacheRank.get(name);
            if (rank != null)
                ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, rank)));
            else
                ctx.status(404).result(response("Rank Not Found", "Rank with the name '" + name + "' does not exist"));
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
                    @OpenApiParam(name = "data", description = "Information to be patched / updated", required = true),
            },
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Requested Rank information is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Rank does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank/:name/:data", method = "GET")
    public static Handler getRankInfo = ctx -> {
        String name = ctx.pathParam("name", String.class).get();
        if (name != null && !name.trim().isEmpty() && !name.matches("[A-Za-z0-9]+")) {
            String pathParam = ctx.pathParam("data", String.class).get();
            String field = convertPathToField(pathParam);
            if (field != null) {
                Rank rank = SQLCacheRank.get(name);
                if (rank != null) {
                    Field rankField = rank.getClass().getDeclaredField(field);
                    rank = wipeAllExceptField(rank,rankField);
                    ctx.status(200).result(GSON.toJson(rank));
                } else
                    ctx.status(404).result(response("Rank Not Found", "Rank with the name '" + name + "' does not exist"));
            } else
                ctx.status(400).result(response("Bad Request", "Invalid data field"));
        } else
            ctx.status(400).result(response("Bad Request", "Name is not valid"));
    };

    @OpenApi(
            summary = "Get a list of all Ranks",
            description = "Get a list of all ranks, query filtering is enabled, Max amount per request is set on auth token permissions",
            tags = {"Rank"},
            queryParams = {
                    @OpenApiParam(name = "name", description = "Filter based on name, full or partial"),
                    @OpenApiParam(name = "permission", description = "Filter based on perms, single or multiple nodes"),
                    @OpenApiParam(name = "inheritance", description = "Filter based on inheritances"),
                    @OpenApiParam(name = "prefix", description = "Filter based on prefix"),
                    @OpenApiParam(name = "prefixPriority", description = "Filter based on prefix priority", type = Integer.class),
                    @OpenApiParam(name = "suffix", description = "Filter based on suffix priority"),
                    @OpenApiParam(name = "suffixPriority", description = "Filter based on suffix priority", type = Integer.class),
                    @OpenApiParam(name = "color", description = "Filter based on color"),
                    @OpenApiParam(name = "colorPriority", description = "Filter based on color priority", type = Integer.class),
            },
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Rank[].class)}, description = "Rank has been returned successfully,"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse.class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank", method = "GET")
    public static Handler getRanks = ctx -> {

    };

    @OpenApi(
            summary = "Delete a rank via name",
            description = "Delete a rank via name",
            tags = {"Rank"},
            pathParams = {@OpenApiParam(name = "name", description = "name of the a given Rank", required = true)},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Deleted Rank is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Rank does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank/:name", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler deleteRank = ctx -> {

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
            errors.add(new MessageResponse("Bad Request", "Name must be alpha-numeric with a size of 1 or greater"));
        // Prefix
        if (!rank.prefix.trim().isEmpty() && !rank.prefix.matches("[A-Za-z0-9&_()*]+"))
            errors.add(new MessageResponse("Bad Request", "Prefix must be alpha-numeric / &_() or *"));
        // Suffix
        if (!rank.suffix.trim().isEmpty() && !rank.suffix.matches("[A-Za-z0-9&_()*]+"))
            errors.add(new MessageResponse("Bad Request", "Suffix must be alpha-numeric / &_() or *"));
        if (errors.size() > 0) {
            ctx.status(400).result(GSON.toJson(errors));
            return false;
        }
        return true;
    }

    /**
     * Removes the data, the given account does not have access to
     *
     * @param ctx  context of the message
     * @param rank
     * @return
     */
    // TODO Implement
    private static Rank filterBasedOnPerms(Context ctx, Rank rank) {
        return rank;
    }

    /**
     * Converts a path param into its name for use as a field / lookup
     *
     * @param data path param to be converted
     * @return Converts a path param into its field name, in Rank
     * @see Rank
     */
    public static String convertPathToField(String data) {
        if (data.equalsIgnoreCase("rank-id") || data.equalsIgnoreCase("id"))
            return "rankID";
        else if (data.equalsIgnoreCase("name"))
            return "name";
        else if (data.equalsIgnoreCase("permissions") || data.equalsIgnoreCase("perms") || data.equalsIgnoreCase("permission"))
            return "permissions";
        else if (data.equalsIgnoreCase("inheritance") || data.equalsIgnoreCase("inheritances"))
            return "inheritance";
        else if (data.equalsIgnoreCase("prefix"))
            return "prefix";
        else if (data.equalsIgnoreCase("prefix-priority") || data.equalsIgnoreCase("prefixPriority"))
            return "prefixPriority";
        else if (data.equalsIgnoreCase("suffix"))
            return "suffix";
        else if (data.equalsIgnoreCase("suffix-priority") || data.equalsIgnoreCase("suffixPriority"))
            return "prefixPriority";
        else if (data.equalsIgnoreCase("color"))
            return "color";
        else if (data.equalsIgnoreCase("color-priority") || data.equalsIgnoreCase("colorPriority"))
            return "colorPriority";
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
            if (!field.equals(safe))
                field.set(rank, null);
        if (safe.get(rank) instanceof String && ((String) safe.get(rank)).isEmpty())
            safe.set(rank, "");
        return rank;
    }
}
