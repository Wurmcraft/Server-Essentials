package io.wurmatron.serveressentials.routes;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.AuthUser;
import io.wurmatron.serveressentials.models.LoginEntry;
import io.wurmatron.serveressentials.models.MessageResponse;

import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class EndpointSecurity {

    /**
     * Gets the permission for a given request
     *
     * @param ctx information about the given request
     * @return the permission level of this request
     */
    public static Route.RestRoles getRole(Context ctx) {
        if (ServerEssentialsRest.config.general.testing)
            return Route.RestRoles.DEV;
        String auth = ctx.cookie("authentication"); // TODO may change
        if (auth != null)
            return Route.RestRoles.SERVER;
        return Route.RestRoles.ANONYMOUS;
    }

    /**
     * Used by javalin to verify that a request has the permission to access the specific route
     *
     * @param app instance of the routes handler
     */
    public static void addSecurityManaging(Javalin app) {
        app.config.accessManager((handler, ctx, permittedRoles) -> {
            Route.RestRoles authRoles = getRole(ctx);
            if (permittedRoles.contains(authRoles))
                handler.handle(ctx);
            else
                ctx.contentType("application/json").status(401).result(response("Unauthorized", "You dont have permission to access this!"));
        });
    }

    @OpenApi(
            summary = "Generates a token for the provided credentials",
            description = "Validates credentials and returns a valid token to be used for authentication throughout the api",
            tags = {"Security"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = LoginEntry.class)}, required = true, description = "Information required for login, slightly different requirements per type"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AuthUser.class)}, description = "User you logged in as, along with its permissions, expiration and more information"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Json / Request"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Credentials"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Credentials, Same as 401 however its details are more precise, such as lock account or already logged in"),
            }
    )
    // TODO Implement
    @Route(path = "api/login", method = "POST", roles = {Route.RestRoles.ANONYMOUS})
    public static Handler login = ctx -> {

    };

    @OpenApi(
            summary = "Removes a auth token from the cache, allowing the account to be logged in again",
            description = "Removes a auth token from the cache, allowing the account to be logged in again",
            tags = {"Security"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AuthUser.class)}, description = "User you logged in as, along with its permissions, expiration and more information"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Json / Request"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Credentials"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Credentials, Same as 401 however its details are more precise, such as lock account or already logged in"),
            }
    )
    // TODO Implement
    @Route(path = "api/logout", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER})
    public static Handler logout = ctx -> {

    };

    @OpenApi(
            summary = "Extends permissions / timeout for token",
            description = "Extends permissions / timeout for token",
            tags = {"Security"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AuthUser.class)}, description = "Updated Token / Account Information"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Json / Request"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Credentials"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Invalid Credentials, Same as 401 however its details are more precise, such as lock account or already logged in"),
            }
    )
    @Route(path = "api/reauth", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER})
    public static Handler reauth = ctx -> {

    };
}
