package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;

public class AutoRankRoutes {

    @OpenApi (
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

    };

    @OpenApi (
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

    };

    @OpenApi (
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
    @Route(path = "api/autorank/:id", method = "GET")
    public static Handler getID = ctx -> {

    };

    @OpenApi (
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
    @Route(path = "api/autorank/:id/:data", method = "GET")
    public static Handler getData = ctx -> {

    };

    @OpenApi (
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
    @Route(path = "api/autorank", method = "GET")
    public static Handler get = ctx -> {

    };


    @OpenApi (
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
    @Route(path = "api/autorank/:id/:data", method = "PATCH", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler patch = ctx -> {

    };

    @OpenApi (
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
    @Route(path = "api/autorank/:id", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {

    };
}
