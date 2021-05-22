package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Action;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;

public class ActionRoutes {

    @OpenApi(
            summary = "Create / log a new action",
            description = "Create / log a new action",
            tags = {"Action"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Action.class)}, required = true, description = "Information about the action"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Action.class)}, description = "Action instance of to created / logged"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "409", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Action already exists"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/action", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {

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
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Action[].class)}, description = "List of actions based on your requested filters (query)"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Action does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/action", method = "GET")
    public static Handler get = ctx -> {

    };

    @OpenApi(
            summary = "Update an existing action",
            description = "Update an existing action",
            tags = {"Action"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Action.class)}, required = true, description = "Information about the action"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Action.class)}, description = "Updated action instance is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Action does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO implement
    @Route(path = "api/action", method = "PUT")
    public static Handler update = ctx -> {

    };

    @OpenApi(
            summary = "Delete an existing action",
            description = "Delete an existing action",
            tags = {"Action"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Action.class)}, required = true, description = "Information about the action"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Action.class)}, description = "Deleted action is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Action does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/action", method = "DELETE")
    public static Handler delete = ctx -> {

    };
}
