package com.wurmcraft.server_essentials.rest.routes;

import com.google.gson.JsonSyntaxException;
import com.wurmcraft.server_essentials.rest.SE_Rest;
import com.wurmcraft.server_essentials.rest.api.NetworkUser;
import com.wurmcraft.server_essentials.rest.sql.ParamChecker;
import com.wurmcraft.server_essentials.rest.sql.SQLCommands;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static com.wurmcraft.server_essentials.rest.SE_Rest.GSON;
import static com.wurmcraft.server_essentials.rest.SE_Rest.LOG;
import static com.wurmcraft.server_essentials.rest.sql.SQLCommands.getUserByUUID;

public class UserController {

    public static String[] USERS_TABLE_COLUMS = {"uuid", "username", "rank"};

    @OpenApi(
            description = "Create a new user",
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
            description = "Get a given user by UUID",
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
            description = "Get a given user by Username",
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

    @OpenApi(
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = NetworkUser.class)),
            responses = {
                    @OpenApiResponse(status = "200", description = "User Updated, DB Updated"),
                    @OpenApiResponse(status = "400", description = "Invalid/Incomplete Json"),
                    @OpenApiResponse(status = "400", description = "Invalid/Incomplete Json"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "404", description = "User does not exist!"),
                    @OpenApiResponse(status = "422", description = "Specify fields to update"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            },
            queryParams = {
                    @OpenApiParam(name = "username", type = Boolean.class),
                    @OpenApiParam(name = "rank", type = Boolean.class)
            }
    )
    public static Handler updateUser = ctx -> {
        try {
            String uuid = ParamChecker.sanitizeUUID(ctx.pathParam("uuid"));
            NetworkUser dbUser = SQLCommands.getUserByUUID(uuid);
            if (dbUser != null) {
                Map<String, List<String>> queryParams = ctx.queryParamMap();
                if (!queryParams.isEmpty()) {
                    try {
                        NetworkUser updatedUser = GSON.fromJson(ctx.body(), NetworkUser.class);
                        StringBuilder builder = new StringBuilder();
                        builder.append("UPDATE `users` SET ");
                        for (String key : USERS_TABLE_COLUMS) {
                            if (key.equals("uuid")) {
                                continue;
                            }
                            if (queryParams.containsKey(key.toLowerCase()) && ParamChecker.returnBool(queryParams.get(key.toLowerCase()).get(0))) {
                                builder.append("`%TYPE%`='%VALUE%', ".replaceAll("%TYPE%", key).replaceAll("%VALUE%", ParamChecker.getNetworkUserInfo(updatedUser, key)));
                            }
                        }
                        builder.append("WHERE uuid='%UUID%';".replaceAll("%UUID%", uuid));
                        String query = builder.toString();
                        query = query.replaceAll(", WHERE", " WHERE");  // Remove trailing
                        try {
                            Statement statement = SE_Rest.connector.getConnection().createStatement();
                            statement.execute(query);
                            ctx.status(200).result(GSON.toJson(SQLCommands.getUserByUUID(uuid)));
                        } catch (Exception e) {
                            LOG.error("user#updateUser: " + ctx.body() + " => " + e.getLocalizedMessage());
                            ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
                        }
                    } catch (JsonSyntaxException e) {
                        ctx.status(400).result("{\"title\": \"Invalid User Json\", \"status\": 409, \"type\": \"\", \"details\": []}");
                    }
                } else {
                    ctx.status(422).result("{\"title\": \"User (\"" + uuid + "\") was not updated, you must specify which fields to update!\", \"status\": 422, \"type\": \"\", \"details\": []}");
                }
            } else {
                ctx.status(404).result("{\"title\": \"User (\"" + uuid + "\") not found!\", \"status\": 404, \"type\": \"\", \"details\": []}");
            }
        } catch (SQLException e) {
            LOG.error("user#updateUser: " + ctx.body() + " => " + e.getLocalizedMessage());
            ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
        }
    };


    public static Handler getUsers = ctx -> {
    };

    @OpenApi(
            description = "Delete the given user by UUID",
            responses = {
                    @OpenApiResponse(status = "200", description = "User has been removed from the DB"),
                    @OpenApiResponse(status = "404", description = "User does not exist"),
                    @OpenApiResponse(status = "401", description = "Unauthorized, Invalid Auth Key"),
                    @OpenApiResponse(status = "422", description = "Invalid UUID"),
                    @OpenApiResponse(status = "518", description = "Basically 418, but with a 5, Something terrible has happened!"),
            }
    )
    public static Handler deleteUser = ctx -> {
        try {
            String uuid = ParamChecker.sanitizeUUID(ctx.pathParam("uuid"));
            if (!uuid.isEmpty()) {
                NetworkUser user = SQLCommands.getUserByUUID(uuid);
                if (user != null) {
                    String query = "DELETE FROM `users` WHERE `uuid`='" + uuid + "';";
                    try {
                        Statement statement = SE_Rest.connector.getConnection().createStatement();
                        statement.execute(query);
                        ctx.status(200).result("{}");
                    } catch (SQLException e) {
                        LOG.error("user#deleteUser: " + ctx.body() + " => " + e.getLocalizedMessage());
                        ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
                    }
                } else {
                    ctx.status(404).result("{\"title\": \"User (\"" + uuid + "\") not found!\", \"status\": 404, \"type\": \"\", \"details\": []}");
                }
            } else {
                ctx.status(422).result("{\"title\": \"" + uuid + "is not a valid uuid!\", \"status\": 422, \"type\": \"\", \"details\": []}");
            }
        } catch (Exception e) {
            LOG.error("user#deleteUser: " + ctx.body() + " => " + e.getLocalizedMessage());
            ctx.status(518).result("{\"title\": \"My tea got overcooked!\", \"status\": 518, \"type\": \"\", \"details\": []}");
        }
    };
}
