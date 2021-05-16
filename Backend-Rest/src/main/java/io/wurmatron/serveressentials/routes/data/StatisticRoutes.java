package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.TrackedStat;
import io.wurmatron.serveressentials.routes.Route;

public class StatisticRoutes {

    @OpenApi(
            summary = "Creates a new statistic entry",
            description = "Creates a new statistical entry into the database",
            tags = {"Statistics"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = TrackedStat.class)}, description = "Statistic Entry has been created successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "409", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Stat Entry already exists"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/statistics", method = "POST", roles = {Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {

    };

    @OpenApi(
            summary = "Override an existing statistic entry",
            description = "Completely overwrite an existing entry in the database",
            tags = {"Statistics"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TrackedStat.class)}, description = "Statistic Entry has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Entry does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/statistics", method = "PUT", roles = {Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {

    };

    @OpenApi(
            summary = "Find a specific tracked statistic about a given server or player, via query params",
            description = "Find a specific entry, based on the filters",
            tags = {"Statistics"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TrackedStat[].class)}, description = "Statistic Entry that match, based on the provided filters"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs"),
            }
    )
    // TODO Implement
    @Route(path = "/statistics", method = "GET")
    public static Handler get = ctx -> {

    };

    @OpenApi(
            summary = "Update a specific entry from a transfer entry",
            description = "Update a specific entry from a transfer entry",
            tags = {"Statistics"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TrackedStat.class)}, description = "Statistic Entry has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/statistics/:data", method = "PATCH", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler patch = ctx -> {

    };

    @OpenApi(
            summary = "Delete a specific transfer entry",
            description = "Update a specific entry from a transfer entry",
            tags = {"Statistics"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TrackedStat.class)}, description = "Statistic Entry has been updated deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/statistics", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {

    };
}
