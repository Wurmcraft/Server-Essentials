package io.wurmatron.serveressentials.routes.data;


import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.MarketEntry;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;

public class MarketRoutes {

    @OpenApi(
            summary = "Create a new market entry with the provided information",
            description = "Create a new market entry with the provided information",
            tags = {"Market"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = MarketEntry.class)}, required = true, description = "Market Entry information used to create the new entry"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = MarketEntry.class)}, description = "Market Entry has been created successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "market", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {

    };

    @OpenApi(
            summary = "Get a array / list of market entries that are based on your query parameters",
            description = "Get a array / list of market entries that are based on your query parameters",
            tags = {"Market"},
            queryParams = {
                    @OpenApiParam(name = "server-id", description = "ID of the server that the market entry was created on"),
                    @OpenApiParam(name = "uuid", description = "UUID of the seller that created the market entry"),
                    @OpenApiParam(name = "market-type", description = "Type of market the entry was created within"),
                    @OpenApiParam(name = "transfer-id", description = "ID of the transfer system the market entry was created within, empty is considered global"),
                    @OpenApiParam(name = "item", description = "Item that this market entry is dealing with"),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MarketEntry[].class)}, description = "Requested Market Entries are returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "market", method = "GET")
    public static Handler get = ctx -> {

    };

    @OpenApi(
            summary = "Override / Update an existing market entry",
            description = "Override / Update an existing market entry",
            tags = {"Market"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = MarketEntry.class)}, required = true, description = "Market Entry information used to update the existing entry"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MarketEntry.class)}, description = "Market Entry has been updated"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to find existing market entry, does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "market", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {

    };

    @OpenApi(
            summary = "Delete an existing market entry",
            description = "Delete an existing market entry",
            tags = {"Market"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = MarketEntry.class)}, required = true, description = "Market Entry that you want to delete"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MarketEntry.class)}, description = "Market Entry has been deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to find existing market entry, does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "market", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {

    };
}
