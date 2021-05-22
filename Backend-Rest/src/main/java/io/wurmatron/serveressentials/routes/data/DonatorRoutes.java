package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Donator;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;

public class DonatorRoutes {

    @OpenApi(
            summary = "Create a new donation entry",
            description = "Create a new donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Donator.class)}, required = true, description = "Information about the donation"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Donator.class)}, description = "Donation Entry has been created successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/donator", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {

    };

    @OpenApi(
            summary = "Get a list of donation entries, based on the specified query data / filters",
            description = "Create a new donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            queryParams = {
                    @OpenApiParam(name = "store", description = "Name of the store used in the entry"),
                    @OpenApiParam(name = "amount", description = "amount of money used to purchase the entry", type = Double.class),
                    @OpenApiParam(name = "uuid", description = "UUID of the amount that purchased this entry"),
                    @OpenApiParam(name = "type", description = "type of the entry"),
                    @OpenApiParam(name = "transaction", description = "ID of the transaction for the entry"),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Donator[].class)}, description = "Donation Entries that match the query"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    // TODO Implement
    @Route(path = "api/donator", method = "GET")
    public static Handler get = ctx -> {

    };

    @OpenApi(
            summary = "Update an existing donation entry",
            description = "Update an existing donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Donator.class)}, required = true, description = "Updated Information about the donation"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Donator.class)}, description = "Donation Entry has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/donator", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {

    };

    @OpenApi(
            summary = "Delete an existing donation entry",
            description = "Update an existing donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Donator.class)}, required = true, description = "Donation entry to be deleted"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Donator.class)}, description = "Donation Entry has been updated deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/donator", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {

    };
}
