package io.wurmatron.serveressentials.routes.data;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAutoRank;

import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class AutoRankRoutes {

    @OpenApi(
            summary = "Create a new auto-rank",
            description = "Create a new auto-rank",
            tags = {"Auto-Rank"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = AutoRank.class)}, required = true, description = "Auto-Rank Information used to create the new entry"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = AutoRank.class)}, description = "Auto-Rank has been created successfully, autoRankID is also returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "409", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Auto-Rank already exists"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/autorank", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {
        try {
            AutoRank autoRank = GSON.fromJson(ctx.body(), AutoRank.class);
            if (isValidAutoRank(ctx, autoRank)) {
                // Check for existing
                AutoRank existing = SQLCacheAutoRank.getName(autoRank.rank);
                if (existing == null) {
                    // Create new AutoRank
                    autoRank = SQLCacheAutoRank.create(autoRank);
                    if (autoRank == null) {
                        ctx.status(500).result(response("AutoRank Failed to create", "Autorank has failed to be created"));
                    }
                    ctx.status(201).result(GSON.toJson(autoRank));
                } else
                    ctx.status(409).result(response("AutoRank Exists", "AutoRank with the same rank exists!"));
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Failed to parse the body into an AutoRank"));
        }
    };

    @OpenApi(
            summary = "Override an existing auto-rank",
            description = "Override an existing auto-rank",
            tags = {"Auto-Rank"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = AutoRank.class)}, required = true, description = "Auto-Rank Information used to update the existing entry"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AutoRank.class)}, description = "Auto-Rank has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Auto-Rank does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/autorank/:id", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {
        try {
            AutoRank autoRank = GSON.fromJson(ctx.body(), AutoRank.class);
            String id = ctx.pathParam("id");
            long arID;
            try {
                arID = Long.parseLong(id);
            } catch (Exception e) {
                ctx.status(400).result(response("Bad Request", "ID is not a valid number"));
                return;
            }
            if (arID != autoRank.autoRankID) {
                ctx.status(400).result(response("Bad Request", "Path ID and Body ID don't match"));
                return;
            }
            if (isValidAutoRank(ctx, autoRank)) {
                // Check for existing
                AutoRank existing = SQLCacheAutoRank.getID(autoRank.autoRankID);
                if (existing != null) {
                    // Update Existing Autorank
                    boolean updated = SQLCacheAutoRank.update(autoRank, new String[]{"rank", "nextRank", "playTime", "currencyName", "currencyAmount", "specialEvents"});
                    if (!updated) {
                        ctx.status(500).result(response("AutoRank failed to update", "Autorank has failed to be updated"));
                    }
                    ctx.status(200).result(GSON.toJson(autoRank));
                } else
                    ctx.status(404).result(response("AutoRank does not exists", "AutoRank with the id does not exist!"));
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Failed to parse the body into an AutoRank"));
        }
    };

    @OpenApi(
            summary = "Get an existing auto-rank based on its ID",
            description = "Get an existing auto-rank based on its ID",
            tags = {"Auto-Rank"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AutoRank.class)}, description = "Requested AutoRank is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Auto-Rank with the provided ID does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/autorank/:id", method = "GET")
    public static Handler getID = ctx -> {

    };

    @OpenApi(
            summary = "Get an existing auto-ranks specific data based on its ID",
            description = "Get an existing auto-ranks specific data based on its ID",
            tags = {"Auto-Rank"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AutoRank.class)}, description = "Requested AutoRank is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Auto-Rank with the provided ID does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/autorank/:id/:data", method = "GET")
    public static Handler getData = ctx -> {

    };

    @OpenApi(
            summary = "Get existing auto-ranks with query filters",
            description = "Get existing auto-ranks with query filters",
            tags = {"Auto-Rank"},
            queryParams = {
                    @OpenApiParam(name = "rank", description = "Rank the user currently has"),
                    @OpenApiParam(name = "next", description = "The rank the user will rank-up into"),
                    @OpenApiParam(name = "playtime", description = "Total Playtime until rank-up"),
                    @OpenApiParam(name = "playtime", description = "Total Playtime until rank-up", type = Long.class),
                    @OpenApiParam(name = "currency", description = "Name of the currency used to rank-up"),
                    @OpenApiParam(name = "currency-amount", description = "Amount of the currency required to rank-up", type = Double.class),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AutoRank[].class)}, description = "Auto-Ranks matching the filter are returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Auto-Rank with the provided ID does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/autorank", method = "GET")
    public static Handler get = ctx -> {

    };

    @OpenApi(
            summary = "Update an existing auto-rank",
            description = "Update an existing auto-rank",
            tags = {"Auto-Rank"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = AutoRank.class)}, required = true, description = "Auto-Rank Information used to update the existing entry"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AutoRank.class)}, description = "Auto-Rank has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Auto-Rank does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/autorank/:id/:data", method = "PATCH", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler patch = ctx -> {

    };

    @OpenApi(
            summary = "Delete an existing auto-rank",
            description = "Delete an existing auto-rank",
            tags = {"Auto-Rank"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = AutoRank.class)}, required = true, description = "Auto-Rank Information used to delete the existing entry"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AutoRank.class)}, description = "Auto-Rank has been updated deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "409", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Rank already exists"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/autorank/:id", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {

    };

    /**
     * Checks if the provided autorank is valid, if not respond with the error
     *
     * @param context  context of the message
     * @param autoRank instance of the provided outrank to check
     */
    public static boolean isValidAutoRank(Context context, AutoRank autoRank) {
        List<MessageResponse> errors = new ArrayList<>();
        // Check Rank
        if (autoRank.rank == null || autoRank.rank.trim().isEmpty())
            errors.add(new MessageResponse("Bad Request", "Invalid / Empty Rank"));
        // Check Next Rank
        if (autoRank.nextRank == null || autoRank.nextRank.trim().isEmpty())
            errors.add(new MessageResponse("Bad Request", "Invalid / Empty Next-Rank"));
        // Check playtime
        if (autoRank.playTime != null && autoRank.playTime < 0)
            errors.add(new MessageResponse("Bad Request", "Invalid Playtime, Must be equal or greater than 0"));
        if (autoRank.currencyName != null && !autoRank.currencyName.trim().isEmpty() && autoRank.currencyAmount != null && autoRank.currencyAmount < 0)
            errors.add(new MessageResponse("Bad Request", "Invalid Currency Amount, Must be equal or greater than 0"));
        if (errors.size() == 0) {
            return true;
        } else {
            context.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
        }
        return false;
    }
}
