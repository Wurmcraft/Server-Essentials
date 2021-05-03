package io.wurmatron.serveressentials.routes.data;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAccount;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.Route.RestRoles;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class AccountRoutes {

    @OpenApi(
            summary = "Creates a new user with the provided information",
            description = "Creates a new user account with the provided information, no system perms or password will be set, even if provided",
            tags = {"User"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Account.class)}, required = true, description = "Amount information used to create the requested account, systemPerms, password info will not be set even if provided"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Account.class)}, description = "Account has been created successfully,"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user", method = "POST", roles = {RestRoles.SERVER, RestRoles.DEV})
    public static Handler createAccount = ctx -> {
        try {
            Account newAccount = GSON.fromJson(ctx.body(), Account.class);
            if (isValidAccount(ctx, newAccount)) {
                // Check for existing account
                Account account = SQLCacheAccount.getAccount(newAccount.uuid);
                if (account == null) {
                    // Create new account
                    newAccount = SQLCacheAccount.newAccount(newAccount);
                    if (newAccount == null) {
                        ctx.status(500).result(response("Account Failed to Create", "Account has failed to be created!"));
                        return;
                    }
                    ctx.status(201).result(GSON.toJson(newAccount));
                } else {
                    ctx.status(409).result(response("Account Exists", "Account with the same uuid exists!"));
                }
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Failed to parse the body into an Account"));
        }
    };

    // TODO Implement
    @Route(path = "/user/:uuid", method = "BEFORE")
    public static Handler overrideAccount_AuthCheck = ctx -> {
        ctx.status(501);
    };

    @OpenApi(
            summary = "Overrides the given user information with the provided information",
            description = "Override a user's account with the provided information",
            tags = {"User"},
            pathParams = {@OpenApiParam(name = "uuid", description = "UUID of the a given account", required = true)},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Account.class)}, required = true, description = "Amount information used to update the requested account"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Account has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse.class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "User does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user/:uuid", method = "PUT", roles = {RestRoles.DEV})
    public static Handler overrideAccount = ctx -> {
        if (validateUUID(ctx, true)) {
            String uuid = ctx.pathParam("uuid", String.class).get();
            try {
                Account account = GSON.fromJson(ctx.body(), Account.class);
                // Make sure account uuid and path uuid are the same
                if (account.uuid.equalsIgnoreCase(uuid)) {
                    if (isValidAccount(ctx, account)) {
                        // Update / Override account
                        if (SQLCacheAccount.updateAccount(account, SQLCacheAccount.getColumns())) {
                            ctx.status(200).result(GSON.toJson(SQLCacheAccount.getAccount(account.uuid)));
                        } else
                            ctx.status(500).result(response("Account Failed to Update", "Account Update has failed!"));
                    }
                } else
                    ctx.status(400).result(response("Bad Request", "UUID's don't match, path: '" + uuid + "' and body: '" + account.uuid + "'"));
            } catch (JsonParseException e) {
                ctx.status(422).result(response("Invalid JSON", "Failed to parse the body into an Account"));
            }
        }
    };

    // TODO Implement
    @Route(path = "/user/:uuid/:data", method = "BEFORE")
    public static Handler patchAccount_AuthCheck = ctx -> {
        ctx.status(501);
    };

    @OpenApi(
            summary = "Overrides the given user information with the provided information",
            description = "Override a user's account with the provided information",
            tags = {"User"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "UUID of the a given account", required = true),
                    @OpenApiParam(name = "data", description = "Information to be patched / updated", required = true),
            },
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Account.class)}, required = true, description = "Amount information used to update the requested account"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Account has been updated successfully,"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "User does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user/:uuid/:data", method = "PATCH", roles = {RestRoles.USER, RestRoles.SERVER, RestRoles.DEV})
    public static Handler patchAccount = ctx -> {
        if (validateUUID(ctx, true)) {
            String uuid = ctx.pathParam("uuid", String.class).get();
            String data = ctx.pathParam("data", String.class).get();
            String fieldName = convertPathToField(data);
            if (fieldName == null) {
                ctx.status(400).result(response("Bad Request", data + " is not valid entry for the requested Account"));
                return;
            }
            // Validate the input data
            try {
                Account patchInfo = GSON.fromJson(ctx.body(), Account.class);
                // Check if user exists
                Account account = SQLCacheAccount.getAccount(uuid);
                if (account != null) {
                    Field field = account.getClass().getDeclaredField(fieldName);
                    field.set(account, field.get(patchInfo));
                    if (isValidAccount(ctx, account)) {
                        ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, account)));
                    } else {
                        ctx.status(500).result(response("Account Error", "User Account has failed to be validated!, Full Update / Put is required"));
                    }
                } else
                    ctx.status(404).result(response("Account Not Found", "Account with uuid " + uuid + " does not exist!"));
            } catch (JsonParseException e) {
                ctx.status(422).result(response("Invalid JSON", "Failed to parse the body into an Account"));
            }
        }
    };

    @OpenApi(
            summary = "Get a users information via UUID",
            description = "Gets a users information via UUID",
            tags = {"User"},
            pathParams = {@OpenApiParam(name = "uuid", description = "UUID of the a given account", required = true)},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "User Account is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "User does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user/:uuid", method = "GET")
    public static Handler getAccount = ctx -> {
        if (validateUUID(ctx, true)) {
            String uuid = ctx.pathParam("uuid", String.class).get();
            Account account = SQLCacheAccount.getAccount(uuid);
            if (account != null)
                ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, account)));
            else
                ctx.status(404).result(response("Account Not Found", "Account with uuid " + uuid + " does not exist!"));
        }
    };

    /**
     * Removes the data, the given account does not have access to
     *
     * @param ctx     context of the request
     * @param account account to remove the data from
     * @return account with the data missing / null
     */
    private static Account filterBasedOnPerms(Context ctx, Account account) {
        RestRoles role = EndpointSecurity.getRole(ctx);
        if (role.equals(RestRoles.DEV))
            return account;
        Account clone = account.clone();
        if (role.equals(RestRoles.SERVER)) {
            clone.passwordHash = null;
            clone.passwordSalt = null;
            clone.systemPerms = null;
            return clone;
        }
        if (role.equals(RestRoles.USER)) {
            // TODO Based on SystemPerms
        }
        clone.discordID = null;
        clone.perms = null;
        clone.perks = null;
        clone.muteTime = null;
        clone.trackedTime = null;
        clone.wallet = null;
        clone.rewardPoints = null;
        clone.passwordHash = null;
        clone.passwordSalt = null;
        clone.systemPerms = null;
        return clone;
    }

    // TODO Implement
    @Route(path = "/user/:uuid/:data", method = "BEFORE")
    public static Handler getAccountInformation_AuthCheck = ctx -> {
        ctx.status(501);
    };

    @OpenApi(
            summary = "Get a specific data entry for the given account via UUID",
            description = "Get a specific entry for the given account via UUID",
            tags = {"User"},
            pathParams = {
                    @OpenApiParam(name = "uuid", description = "UUID of the a given account", required = true),
                    @OpenApiParam(name = "data", description = "Information to be patched / updated", required = true),
            },
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Requested Account information is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "User does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user/:uuid/:data", method = "GET")
    public static Handler getAccountInformation = ctx -> {
        if (validateUUID(ctx, true)) {
            String uuid = ctx.pathParam("uuid", String.class).get();
            // Validate Data
            String data = ctx.pathParam("data", String.class).get();
            String field = convertPathToField(data);
            if (field == null) {
                ctx.status(400).result(response("Bad Request", data + " is not valid entry for the requested Account"));
                return;
            }
            Account account = filterBasedOnPerms(ctx, SQLCacheAccount.getAccount(uuid));
            if (account != null) {
                Field accountField = account.getClass().getDeclaredField(field);
                account = wipeAllExceptField(account, accountField);
                ctx.status(200).result(GSON.toJson(account));
            } else
                ctx.status(404).result(response("Account Not Found", "Account with uuid " + uuid + " does not exist!"));
        }
    };

    @OpenApi(
            summary = "Get a list of all Accounts",
            description = "Get a list of all accounts, query filtering is enabled, Max amount per request is set on auth token permissions",
            tags = {"User"},
            queryParams = {
                    @OpenApiParam(name = "uuid", description = "Filter based on UUID, full or partial"),
                    @OpenApiParam(name = "username", description = "Filter based on username, full or partial"),
                    @OpenApiParam(name = "rank", description = "Filter based on rank, has the given rank"),
                    @OpenApiParam(name = "language", description = "Filter based on language of the given account"),
                    @OpenApiParam(name = "muted", description = "Filter based on if the account is muted", type = Boolean.class),
                    @OpenApiParam(name = "discordID", description = "Filter based on discordID, full or partial"),
                    @OpenApiParam(name = "playtimeMin", description = "Filter based on playtime, minimum time", type = Integer.class),
                    @OpenApiParam(name = "playtimeMax", description = "Filter based on playtime, maximum time", type = Integer.class),
                    @OpenApiParam(name = "serverID", description = "Filter based on serverID"),
                    @OpenApiParam(name = "balanceMin", description = "Filter based on balance, minimum amount", type = Double.class),
                    @OpenApiParam(name = "balanceMax", description = "Filter based on balance, maximum amount", type = Double.class),
                    @OpenApiParam(name = "currency", description = "Filter based on type of currency"),
                    @OpenApiParam(name = "rewardPointsMin", description = "Filter based on the amount of rewardPoints, minimum amount", type = Integer.class),
                    @OpenApiParam(name = "rewardPointsMax", description = "Filter based on the amount of rewardPoints, maximum amount", type = Integer.class),
                    @OpenApiParam(name = "limit", description = "Maximum amount of accounts to return", type = Integer.class),
            },
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account[].class)}, description = "Account has been updated successfully,"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse.class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user", method = "GET")
    public static Handler getAccounts = ctx -> {
        String sql = createSQLForUsersWithFilters(ctx);
        // Send Request and Process
        List<Account> accounts = SQLDirect.queryArray(sql, new Account());
        ctx.status(200).result(GSON.toJson(accounts.toArray(new Account[0])));
    };


    // TODO Implement
    @Route(path = "/user/:uuid", method = "BEFORE")
    public static Handler deleteAccount_AuthCheck = ctx -> {
        ctx.status(501);
    };

    @OpenApi(
            summary = "Delete a user's account via UUID",
            description = "Delete a user's account via UUID",
            tags = {"User"},
            pathParams = {@OpenApiParam(name = "uuid", description = "UUID of the a given account", required = true)},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Deleted User Account is returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "User does not exist"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user/:uuid", method = "DELETE", roles = {RestRoles.SERVER, RestRoles.DEV})
    public static Handler deleteAccount = ctx -> {
        if (validateUUID(ctx, true)) {
            String uuid = ctx.pathParam("uuid", String.class).get();
            // Check if account exists
            Account existingAccount = SQLCacheAccount.getAccount(uuid);
            if (existingAccount != null) {
                if (SQLCacheAccount.deleteAccount(uuid))
                    ctx.status(200).result(GSON.toJson(existingAccount));
            } else
                ctx.status(404).result(response("No Account", "No Account exists with the provided UUID"));
        }
    };

    /**
     * Checks if the provided UUID is a valid / possible UUID
     *
     * @param ctx context of the message instance
     * @return if the uuid in the path is a valid uuid
     */
    private static boolean validateUUID(Context ctx, boolean path) {
        String uuid;
        if (path)
            uuid = ctx.pathParam("uuid", String.class).get();
        else {
            uuid = ctx.queryParam("uuid");
            if (uuid == null)
                uuid = "";
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(response("Bad Request", "Param UUID is not valid"));
        }
        return false;
    }

    /**
     * Removes / sets all the fields to null except the one provided
     *
     * @param account instance of account to remove everything from
     * @param safe    field to keep in the account instance
     * @return Account with all but one field has been removed / null'd
     * @throws IllegalAccessException This should never happen, unless Account has been modified
     */
    private static Account wipeAllExceptField(Account account, Field safe) throws IllegalAccessException {
        account = account.clone();
        for (Field field : account.getClass().getDeclaredFields())
            if (!field.equals(safe))
                field.set(account, null);
        if (safe.get(account) instanceof String && ((String) safe.get(account)).isEmpty())
            safe.set(account, "");
        return account;
    }

    /**
     * Converts the endpoint PathParm into he internal data name, used for reflection
     *
     * @param data PathParam provided by the user via the endpoint
     */
    public static String convertPathToField(String data) {
        if (data.equalsIgnoreCase("uuid"))
            return "uuid";
        else if (data.equalsIgnoreCase("username"))
            return "username";
        else if (data.equalsIgnoreCase("rank") || data.equalsIgnoreCase("ranks"))
            return "rank";
        else if (data.equalsIgnoreCase("perm") || data.equalsIgnoreCase("perms"))
            return "perms";
        else if (data.equalsIgnoreCase("perk") || data.equalsIgnoreCase("perks"))
            return "perks";
        else if (data.equalsIgnoreCase("lang") || data.equalsIgnoreCase("language"))
            return "language";
        else if (data.equalsIgnoreCase("mute") || data.equalsIgnoreCase("muted"))
            return "muted";
        else if (data.equalsIgnoreCase("mute-time") || data.equalsIgnoreCase("mutetime"))
            return "muteTime";
        else if (data.equalsIgnoreCase("display-name") || data.equalsIgnoreCase("displayname"))
            return "displayName";
        else if (data.equalsIgnoreCase("discord-id") || data.equalsIgnoreCase("discordid") || data.equalsIgnoreCase("discord"))
            return "discordID";
        else if (data.equalsIgnoreCase("play-time") || data.equalsIgnoreCase("playtime") || data.equalsIgnoreCase("time"))
            return "trackedTime";
        else if (data.equalsIgnoreCase("currency") || data.equalsIgnoreCase("wallet"))
            return "wallet";
        else if (data.equalsIgnoreCase("reward-points") || data.equalsIgnoreCase("rewardpoints"))
            return "rewardPoints";
        else if (data.equalsIgnoreCase("password-hash") || data.equalsIgnoreCase("passwordhash"))
            return "passwordHash";
        else if (data.equalsIgnoreCase("password-salt") || data.equalsIgnoreCase("passwordsalt"))
            return "passwordSalt";
        else if (data.equalsIgnoreCase("system-perms") || data.equalsIgnoreCase("systemperms"))
            return "systemPerms";
        return null;
    }

    /**
     * Checks an accounts info to make sure its valid
     *
     * @param context message context for the request
     * @param account account instance to be checked
     * @return if a account is valid or not
     */
    public static boolean isValidAccount(Context context, Account account) {
        List<MessageResponse> errors = new ArrayList<>();
        // Check for valid UUID
        try {
            UUID.fromString(account.uuid);
        } catch (IllegalArgumentException e) {
            errors.add(new MessageResponse("Bad Request", "Invalid UUID"));
            return false;
        }

        // Validate Username
        if (account.username == null || account.username.trim().isEmpty()) {
            errors.add(new MessageResponse("Bad Request", "Invalid / Empty Username"));
        }

        // Validate Rank
        if (account.rank == null || account.rank.length == 0) {
            errors.add(new MessageResponse("Bad Request", "Missing Rank(s)"));
        }
        // Check for valid ranks
        if (account.rank != null)
            for (String rank : account.rank)
                if (rank.trim().isEmpty()) {
                    errors.add(new MessageResponse("Bad Request", "Empty Rank(s)"));
                } else {
                    Rank validRank = SQLCacheRank.getName(rank);
                    if (validRank == null) {
                        errors.add(new MessageResponse("Bad Request", rank + " is not a valid rank!"));
                    }
                }

        // Validate Perms
        if (account.perms != null && account.perms.length > 0)
            for (String perm : account.perms)
                if (!perm.contains(".") && !perm.equalsIgnoreCase("*"))
                    errors.add(new MessageResponse("Bad Request", perm + " is not a perm!"));

        // Validate Perks
        if (account.perks != null && account.perks.length > 0)
            for (String perk : account.perks)
                if (!perk.contains("."))
                    errors.add(new MessageResponse("Bad Request", perk + " is not a perk!"));

        // Validate Language
        if (account.language == null || account.language.trim().isEmpty()) {
            errors.add(new MessageResponse("Bad Request", "Language must not be empty"));
        }
        // TODO Check for valid language

        if (errors.size() > 0) {
            context.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
            return false;
        }
        return true;
    }

    /**
     *  Generates a SQL Statement for get users with filters applied
     *
     * @param ctx context to get the information from the user
     * @return  sql statement for user lookup
     */
    private static String createSQLForUsersWithFilters(Context ctx) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM " + SQLCacheAccount.USERS_TABLE + " WHERE ");
        // Verify, Check and Apply UUID Filter
        String uuid = ctx.queryParam("uuid");
        if (uuid != null && !uuid.trim().isEmpty())
            sqlBuilder.append("uuid LIKE '").append(uuid).append("%' AND ");
        // Verify, Check and Apply Username Filter
        String username = ctx.queryParam("username");
        if (username != null && !username.trim().isEmpty())
            sqlBuilder.append("username LIKE '").append(username).append("%' AND ");
        // Verify, Check and Apply Rank Filter
        String rank = ctx.queryParam("rank");
        if (rank != null && !rank.trim().isEmpty())
            sqlBuilder.append("rank LIKE '").append(rank).append("%' AND ");
        // Verify, Check and Apply Language Filter
        String language = ctx.queryParam("language");
        if (language != null && !language.trim().isEmpty())
            sqlBuilder.append("language LIKE '").append(language).append("%' AND ");
        // Verify, Check and Apply Muted Filter
        String muted = ctx.queryParam("language");
        if (language != null && !language.trim().isEmpty())
            sqlBuilder.append("muted LIKE '").append(muted).append("' AND ");
        // Verify, Check and Apply DiscordID Filter
        String discordID = ctx.queryParam("discordID");
        if (discordID != null && !discordID.trim().isEmpty())
            sqlBuilder.append("discordID LIKE '").append(discordID).append("%' AND ");
        // TODO Playtime
        // TODO Currency
        String rewardPointsMin = ctx.queryParam("rewardPointsMin");
        if (rewardPointsMin != null && !rewardPointsMin.trim().isEmpty())
            sqlBuilder.append("rewardPoints >= '").append(rewardPointsMin).append("' AND ");
        String rewardPointsMax = ctx.queryParam("rewardPointsMax");
        if (rewardPointsMax != null && !rewardPointsMax.trim().isEmpty())
            sqlBuilder.append("rewardPoints <= '").append(rewardPointsMax).append("' AND ");
        // Finalize SQL
        sqlBuilder.append(";");
        String sql = sqlBuilder.toString();
        if (sql.endsWith("WHERE ;"))
            sql = sql.substring(0, sql.length() - 7);
        if (sql.endsWith(" AND ;"))
            sql = sql.substring(0, sql.length() - 5);
        return sql;
    }
}
