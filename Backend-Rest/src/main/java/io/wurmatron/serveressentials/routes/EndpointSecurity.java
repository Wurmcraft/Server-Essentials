package io.wurmatron.serveressentials.routes;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.wurmatron.serveressentials.ServerEssentialsRest;

import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class EndpointSecurity {

    /**
     * Gets the permission for a given request
     *
     * @param ctx information about the given request
     * @return the permission level of this request
     */
    public static RestRoles getRole(Context ctx) {
        if(ServerEssentialsRest.config.general.testing)
            return RestRoles.DEV;
        String auth = ctx.cookie("authentication"); // TODO may change
        if (auth != null)
            return RestRoles.SERVER;
        return RestRoles.ANONYMOUS;
    }

    /**
     * Used by javalin to verify that a request has the permission to access the specific route
     *
     * @param app instance of the routes handler
     */
    public static void addSecurityManaging(Javalin app) {
        app.config.accessManager((handler, ctx, permittedRoles) -> {
            RestRoles authRoles = getRole(ctx);
            if (permittedRoles.contains(authRoles))
                handler.handle(ctx);
            else
                ctx.contentType("application/json").status(401).result(response("Unauthorized", "You dont have permission to access this!"));
        });
    }
}
