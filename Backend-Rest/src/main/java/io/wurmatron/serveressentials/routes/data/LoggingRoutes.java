package io.wurmatron.serveressentials.routes.data;


import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.LogEntry;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;

public class LoggingRoutes {

    @OpenApi(
            summary = "Create a new logging event",
            description = "Create a new logging event",
            tags = {"Logging"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = LogEntry.class)}, required = true, description = "Information about the log event"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = LogEntry.class)}, description = "Log Entry has been created successfully,"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/logging", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {

    };

    @OpenApi(
            summary = "Filter the log entries based on your query",
            description = "Filter the log entries based on your query",
            tags = {"Logging"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            queryParams = {
                    @OpenApiParam(name = "server-id", description = "ID of the server where this event took place"),
                    @OpenApiParam(name = "action", description = "Type of action that has taken place"),
                    @OpenApiParam(name = "uuid", description = "UUID of the user / account that caused this event"),
                    @OpenApiParam(name = "x", description = "X Position that this event took place", type = Integer.class),
                    @OpenApiParam(name = "y", description = "Y Position that this event took place", type = Integer.class),
                    @OpenApiParam(name = "z", description = "Z Position that this event took place", type = Integer.class),
                    @OpenApiParam(name = "dim", description = "Dimension that this event took place", type = Integer.class)
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = LogEntry[].class)}, description = "Log Entry's that fit into your required filters (query)"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/logging", method = "GET")
    public static Handler get = ctx -> {

    };

    @OpenApi(
            summary = "Update an existing log event",
            description = "Update an existing log event",
            tags = {"Logging"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = LogEntry.class)}, required = true, description = "Information about the log event"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = LogEntry.class)}, description = "Log Entry has been successfully updated"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/logging", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {

    };

    @OpenApi(
            summary = "Remove an existing log event",
            description = "Remove an existing log event",
            tags = {"Logging"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = LogEntry.class)}, required = true, description = "The event entry you want to remove"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = LogEntry.class)}, description = "Log Entry has been successfully deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/logging", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {

    };
}
