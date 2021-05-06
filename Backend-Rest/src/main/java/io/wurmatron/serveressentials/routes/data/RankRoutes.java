package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.routes.Route;

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
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/rank", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler createRank = ctx -> {

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
}
