package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.routes.Route;

public class TransferRoutes {

    @OpenApi(
            summary = "Creates a new entry with provided information",
            description = "Creates a new entry for the provided user with the provided information",
            tags = {"Transfer"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to be used for authentication within the rest api")},
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = TransferEntry.class)}, description = "Transfer Entry has been created successfully, rankID is also returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "409", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Transfer Entry already exists"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/transfer", method = "POST", roles = {Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {

    };

    @OpenApi(
            summary = "Find a list of transfer entries that's filtered based on the provided parameters",
            description = "Find a list of transfer entries that's filtered based on the provided parameters",
            tags = {"Transfer"},
            queryParams = {
                    @OpenApiParam(name = "uuid", description = "Full or Partial UUID of the account to filter by"),
                    @OpenApiParam(name = "serverID", description = "Full or Partial Server ID to filter by"),
                    @OpenApiParam(name = "start-time", description = "Starting Time when the entry was created")
            },
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = TransferEntry[].class)}, description = "List of all the Transfer "),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/transfer", method = "GET", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler get = ctx -> {

    };

    @OpenApi(
            summary = "Find a specific transfer entry based on its ID",
            description = "Find a specific transfer entry based on its ID",
            tags = {"Transfer"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TransferEntry.class)}, description = "Returns the requested transfer entry"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Requested ID does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/transfer/:id", method = "GET", roles = {Route.RestRoles.ANONYMOUS, Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler getID = ctx -> {

    };

    @OpenApi(
            summary = "Find a specific transfer entry based on its ID and the requested data",
            description = "Find a specific transfer entry based on its ID and the requested data",
            tags = {"Transfer"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TransferEntry.class)}, description = "Returns the requested transfer entry"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Requested ID does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/transfer/:id/:data", method = "GET", roles = {Route.RestRoles.ANONYMOUS, Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler getData = ctx -> {

    };

    @OpenApi(
            summary = "Update / Override an existing transfer entry",
            description = "Update / Override an existing transfer entry",
            tags = {"Transfer"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to be used for authentication within the rest api")},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TransferEntry.class)}, description = "Updated transfer entry"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Requested ID does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/transfer/:id", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler update = ctx -> {

    };

    @OpenApi(
            summary = "Update the specified entry in the transfer entry",
            description = "Update the specified entry in the transfer entry",
            tags = {"Transfer"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TransferEntry.class)}, description = "Updated transfer entry"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Requested ID does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "/transfer/:id/:data", method = "PATCH", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler patch = ctx -> {

    };

    @OpenApi(
            summary = "Delete the specified transfer entry",
            description = "Delete a specific transfer entry",
            tags = {"Transfer"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = TransferEntry.class)}, description = "Transfer Entry that was deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Requested ID does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/transfer/:id", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {

    };
}
