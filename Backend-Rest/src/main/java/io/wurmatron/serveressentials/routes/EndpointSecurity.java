package io.wurmatron.serveressentials.routes;

import com.google.common.io.Files;
import com.google.gson.JsonParseException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.AuthUser;
import io.wurmatron.serveressentials.models.LoginEntry;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.data.ServerAuth;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAccount;
import io.wurmatron.serveressentials.utils.EncryptionUtils;
import io.wurmatron.serveressentials.utils.FileUtils;
import io.wurmatron.serveressentials.utils.PermissionValidator;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.time.Instant;

import java.util.*;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.SAVE_DIR;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class EndpointSecurity {

    public static NonBlockingHashMap<String, AuthUser> authTokens = new NonBlockingHashMap<>();
    public static final SecureRandom RAND = new SecureRandom();
    // Config
    public static final int USER_TOKEN_SIZE = 64;
    public static final long USER_TIMEOUT = 24 * 60 * 60 * 1000;  // 1d
    public static final int SERVER_TOKEN_SIZE = 128;
    public static final int SERVER_TIMEOUT = 5 * 60 * 1000; // 5m
    // Perm Cache
    public static NonBlockingHashMap<String, String> permCache = new NonBlockingHashMap<>();
    // Tokens / Security / Internal Managing
    public static final File INTERNAL_DIR = new File(SAVE_DIR + File.separator + "internal");
    public static NonBlockingHashMap<String, ServerAuth> serverAuth = new NonBlockingHashMap<>();

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
        if (auth != null && authTokens.contains(auth)) {
            AuthUser user = authTokens.get(auth);
            if (user.type.equalsIgnoreCase("USER"))
                return Route.RestRoles.USER;
            if (user.type.equalsIgnoreCase("SERVER"))
                return Route.RestRoles.SERVER;
        }
        return Route.RestRoles.ANONYMOUS;
    }

    /**
     * Loads the server tokens, or create the empty file if it does not exist
     */
    private static void loadServerTokens() {
        File serverTokens = new File(INTERNAL_DIR + File.separator + "servers.json");
        if (serverTokens.exists()) {
            try {
                List<String> lines = Files.readLines(serverTokens, Charset.defaultCharset());
                for (String line : lines) {
                    String[] split = line.split(":");
                    String serverID = split[0];
                    String token = new String(Base64.getDecoder().decode(split[1]));
                    String key = new String(Base64.getDecoder().decode(split[2]));
                    String ip = new String(Base64.getDecoder().decode(split[3]));
                    ServerAuth auth = new ServerAuth(serverID, token, key, ip);
                    serverAuth.put(serverID, auth);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                serverTokens.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the pepper value for use within the DB and other encryption uses
     */
    private static void loadPepper() {
        try {
            File pepper = new File(INTERNAL_DIR + File.separator + "pepper.txt");
            if (pepper.exists())
                EncryptionUtils.pepper = Files.readFirstLine(pepper, Charset.defaultCharset());
            else {
                String newPepper = generateToken(32);
                FileUtils.write(pepper, newPepper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        loadPepper();
        loadServerTokens();
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
    @Route(path = "api/login", method = "POST", roles = {Route.RestRoles.ANONYMOUS})
    public static Handler login = ctx -> {
        try {
            AuthUser authUser = GSON.fromJson(ctx.body(), AuthUser.class);
            if (validateLogin(ctx, authUser)) {
                if (authUser.type.equalsIgnoreCase("USER")) {
                    Account account = SQLCacheAccount.get(authUser.name);
                    if (account != null) {
                        if (EncryptionUtils.isSame(account.passwordHash, account.passwordSalt, authUser.token)) {
                            // User will now login
                            String userToken = generateToken(USER_TOKEN_SIZE);
                            authUser.token = userToken;
                            authUser.key = "";
                            authUser.type = "USER";
                            authUser.perms = account.systemPerms;
                            authUser.expiration = Instant.now().toEpochMilli() + USER_TIMEOUT;
                            authTokens.put(userToken, authUser); // TODO Delete Expired Tokens
                            ctx.status(200).result(GSON.toJson(authUser));
                        } else {
                            ctx.status(403).result(response("Bad Credentials", "Invalid User / Password Combination"));
                        }
                    } else {
                        ctx.status(401).result(response("Bad Credentials", "Authentication Failed"));
                    }
                } else if (authUser.type.equalsIgnoreCase("SERVER")) {
                    if (authUser.token != null && !authUser.token.isEmpty() && authUser.key != null && !authUser.key.isEmpty() && serverAuth.containsKey(authUser.name) && ctx.ip().equalsIgnoreCase(serverAuth.get(authUser.name).ip)) {
                        ServerAuth auth = serverAuth.get(authUser.name);
                        if (authUser.key.equals(auth.key) && authUser.token.equals(auth.token) && authUser.name.equals(auth.serverID)) {
                            // Server will login
                            String serverToken = generateToken(SERVER_TOKEN_SIZE);
                            authUser.token = serverToken;
                            authUser.key = "";
                            authUser.type = "SERVER";
                            authUser.perms = new String[]{"*"};
                            authUser.expiration = Instant.now().toEpochMilli() + SERVER_TIMEOUT;
                            authTokens.put(serverToken, authUser); // TODO Delete Expired Tokens
                            ctx.status(200).result(GSON.toJson(authUser));
                        } else
                            ctx.status(403).result(response("Bad Credentials", "Invalid ID / Key / Token Combination"));
                    } else
                        ctx.status(403).result(response("Bad Credentials", "Invalid ID / Key / Token Combination"));
                }
            }
        } catch (JsonParseException e) {
            ctx.status(400).result(response("Bad Request", "Cannot convert body to Authentication Login"));
        }
    };

    @OpenApi(
            summary = "Removes a auth token from the cache, allowing the account to be logged in again",
            description = "Removes a auth token from the cache, allowing the account to be logged in again",
            tags = {"Security"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = AuthUser.class)}, description = "User you logged out, along with its permissions, expiration and more information"),
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

    /**
     * Checks if the request is a possible valid login request, before checking for login
     *
     * @param ctx  context of the message
     * @param user instance of the login request
     * @return if the account is valid or not
     */
    private static boolean validateLogin(Context ctx, AuthUser user) {
        // Validate login type
        if (!user.type.equalsIgnoreCase("USER") && !user.type.equalsIgnoreCase("SERVER")) {
            ctx.status(400).result(response("Bad Request", "Invalid User Login Type"));
            return false;
        }
        // Validate username
        if (user.name.trim().isEmpty()) {
            ctx.status(400).result(response("Bad Request", "Invalid / Empty Name"));
            return false;
        }
        // Validate password / token
        if (user.token.trim().isEmpty()) {
            ctx.status(400).result(response("Bad Request", "Token must not be empty!"));
            return false;
        }
        // Validate key (for SERVER only)
        if (user.type.equalsIgnoreCase("SERVER") && user.key.trim().isEmpty()) {
            ctx.status(400).result(response("Bad Request", "Server's must provide there key upon login request"));
        }
        return true;
    }

    public static final String[] POSSIBLE_VALUES = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    /**
     * Generate a token to be used for authentication
     *
     * @param length Amount of characters in the token
     * @return token with the provided length
     */
    public static String generateToken(int length) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < length; x++)
            builder.append(POSSIBLE_VALUES[RAND.nextInt(POSSIBLE_VALUES.length)]);
        return builder.toString();
    }

    /**
     * @param ctx  context of the message
     * @param perm permission you are trying to check for
     * @return if the user has the provided perm
     */
    public static boolean hasPerms(Context ctx, String perm) {
        Route.RestRoles role = getRole(ctx);
        if (role.equals(Route.RestRoles.USER)) {
            String auth = ctx.cookie("authentication"); // TODO may change
            AuthUser user = authTokens.get(auth);
            for (String p : user.perms) {
                if (PermissionValidator.isSamePerm(perm, p)) {
                    return true;
                }
            }
            return false;
        } else if (role.equals(Route.RestRoles.SERVER) || role.equals(Route.RestRoles.DEV))
            return true;
        else
            return false;
    }

    /**
     * Check if the provided message has the required permissions
     *
     * @param ctx context of the message
     */
    public static void checkForPerms(Context ctx) {
        Route.RestRoles role = getRole(ctx);
        if (role.equals(Route.RestRoles.USER)) {
            String perm = getPermNode(ctx.path(), ctx.method());
            if (!hasPerms(ctx, perm)) {
                ctx.status(403).result(response("Not Authorized", "No Perms"));
            }
        }
    }

    /**
     * Generate a permission node based on a endpoints path and http method
     *
     * @param path   endpoint path
     * @param method http method used by the endpoint
     * @return Dynamically generated permission node
     */
    public static String getPermNode(String path, String method) {
        if (permCache.contains(path + "_" + method.toLowerCase())) {
            return permCache.get(path + "_" + method.toLowerCase());
        } else {
            // Dynamically Generate Perm Nodes based on path and method
            String[] split = path.split("/");
            StringBuilder builder = new StringBuilder();
            int startX = 0;
            // Split api if its the first entry
            if (split[0].equalsIgnoreCase("api"))
                startX = 1;
            for (int x = startX; x < split.length; x++) {
                if (split[x].startsWith(":"))
                    break;
                builder.append(split[x]);
                builder.append(".");
            }
            String node = builder + method;
            permCache.put(path + "_" + method.toLowerCase(), node);
            return node;
        }
    }
}
