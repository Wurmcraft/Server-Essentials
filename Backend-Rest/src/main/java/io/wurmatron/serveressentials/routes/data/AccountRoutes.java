package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.wurmatron.serveressentials.routes.Route;

public class AccountRoutes {

    // TODO Implement
    @OpenApi()
    @Route(path = "/user", method = "POST")
    public static Handler createAccount = ctx -> {
        ctx.status(501);
    };
}
