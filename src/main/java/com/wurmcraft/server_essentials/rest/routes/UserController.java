package com.wurmcraft.server_essentials.rest.routes;

import com.google.gson.JsonSyntaxException;
import com.wurmcraft.server_essentials.rest.api.NetworkUser;
import com.wurmcraft.server_essentials.rest.sql.ParamChecker;
import com.wurmcraft.server_essentials.rest.sql.SQLCommands;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;

import java.sql.SQLException;

import static com.wurmcraft.server_essentials.rest.SE_Rest.GSON;
import static com.wurmcraft.server_essentials.rest.SE_Rest.LOG;
import static com.wurmcraft.server_essentials.rest.sql.SQLCommands.getUserByUUID;

public class UserController {

    @OpenApi(
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = NetworkUser.class)),
            responses = {
                    @OpenApiResponse(status = "201", description = "New User Created, Added to DB"),
                    @OpenApiResponse(status = "400", description = "Invalid/Incomplete Json"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "409", description = "User Account exists"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            }
    )
    public static Handler registerUser = ctx -> {
        try {
            NetworkUser user = GSON.fromJson(ctx.body(), NetworkUser.class);
            String uuid = ParamChecker.sanitizeUUID(user.uuid);
            // Check for valid json
            if (uuid.isEmpty() || !ParamChecker.isValid(user)) {
                ctx.status(400).result("{\"title\": \"Invalid UUID / User Json\", \"status\": 409, \"type\": \"\", \"details\": []}");
                return;
            }
            // Check for existing user
            NetworkUser dbUser = null;
            try {
                dbUser = getUserByUUID(uuid);
            } catch (Exception e) {
                LOG.error("user#register: " + ctx.body() + " => " + e.getLocalizedMessage());
                ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
            }
            if (dbUser == null) {    // Create User
                try {
                    SQLCommands.addUser(user);
                    ctx.status(201);
                } catch (Exception e) {
                    LOG.error("user#register: " + ctx.body() + " => " + e.getLocalizedMessage());
                    ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
                }
            } else {    // User Exists
                ctx.status(409).result("{\"title\": \"User with uuid (\" + uuid + \") exists!\", \"status\": 409, \"type\": \"\", \"details\": []}");
            }
        } catch (JsonSyntaxException e) {
            ctx.status(400).result("{\"title\": \"Invalid Json\", \"status\": 409, \"type\": \"\", \"details\": []}");
        }
    };

    @OpenApi(
            responses = {
                    @OpenApiResponse(status = "200", description = "Get requested user data", content = @OpenApiContent(from = NetworkUser.class)),
                    @OpenApiResponse(status = "400", description = "Invalid UUID"),
                    @OpenApiResponse(status = "404", description = "Requested User does not exist!"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            }
    )
    public static Handler getUserUUID = ctx -> {
        String uuid = ParamChecker.sanitizeUUID(ctx.pathParam("uuid"));
        if (!uuid.isEmpty()) {
            try {
                NetworkUser user = SQLCommands.getUserByUUID(uuid);
                if (user != null) {
                    ctx.status(200).result(GSON.toJson(user));
                } else {
                    ctx.status(404).result("{\"title\": \"User (\"" + uuid + "\") not found!\", \"status\": 404, \"type\": \"\", \"details\": []}");
                }
            } catch (SQLException e) {
                LOG.error("user#getUserUUID: " + ctx.body() + " => " + e.getLocalizedMessage());
                ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
            }
        } else {
            ctx.status(400).result("{\"title\": \"Invalid UUID\", \"status\": 400, \"type\": \"\", \"details\": []}");
        }
    };

    @OpenApi(
            responses = {
                    @OpenApiResponse(status = "200", description = "Get requested user data", content = @OpenApiContent(from = NetworkUser.class)),
                    @OpenApiResponse(status = "400", description = "Invalid Username"),
                    @OpenApiResponse(status = "404", description = "Requested User does not exist!"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            }
    )
    public static Handler getUserName = ctx -> {
        String username = ParamChecker.sanitizeName(ctx.pathParam("name"));
        if (!username.isEmpty()) {
            try {
                NetworkUser user = SQLCommands.getUserByName(username);
                if (user != null) {
                    ctx.status(200).result(GSON.toJson(user));
                } else {
                    ctx.status(404).result("{\"title\": \"User (\"" + username + "\") not found!\", \"status\": 404, \"type\": \"\", \"details\": []}");
                }
            } catch (SQLException e) {
                LOG.error("user#getUserName: " + ctx.body() + " => " + e.getLocalizedMessage());
                ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
            }
        } else {
            ctx.status(400).result("{\"title\": \"Invalid UUID\", \"status\": 400, \"type\": \"\", \"details\": []}");
        }
    };

    public static Handler updateUser = ctx -> {
    };
    public static Handler getUsers = ctx -> {
    };
    public static Handler deleteUser = ctx -> {
    };
}
