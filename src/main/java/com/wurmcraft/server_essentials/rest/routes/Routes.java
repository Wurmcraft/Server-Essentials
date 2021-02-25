package com.wurmcraft.server_essentials.rest.routes;

import io.javalin.Javalin;

public class Routes {

    public static void register(Javalin app) {
        // User Routes
        app.get("api/user/uuid/:uuid", UserController.getUserUUID);
        app.get("api/user/name/:name", UserController.getUserName);
        app.post("api/user", UserController.registerUser);
        app.put("api/user/:uuid", UserController.updateUser);
//        app.get("api/users", UserController.getUsers);
//        app.delete("api/user/:uuid", UserController.deleteUser);
    }
}
