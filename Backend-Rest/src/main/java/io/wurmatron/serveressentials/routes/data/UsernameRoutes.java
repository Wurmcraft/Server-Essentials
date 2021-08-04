package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheUsername;

import java.util.UUID;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class UsernameRoutes {

    @OpenApi(
            summary = "Get a username via uuid",
            description = "Get a username when provided with a uuid",
            tags = {"Lookup"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Username was returned successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "uuid does not exist within the management system"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/uuid/:uuid", method = "GET", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler get = ctx -> {
        try {
            UUID uuid = UUID.fromString(ctx.pathParam("uuid"));
            String username = SQLCacheUsername.get(uuid.toString());
            if(username != null && !username.isEmpty()) {
                Account usernameAccount = new Account();
                usernameAccount.username = username;
                 ctx.status(200).result(GSON.toJson(usernameAccount));
            }
        } catch (Exception e) {
            ctx.status(400).result(response("Invalid UUID", "provided uuid must be a valid uuid"));
        }
    };
}
