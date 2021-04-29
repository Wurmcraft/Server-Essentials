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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.Route.RestRoles;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class AccountRoutes {

    // TODO Implement
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

    public static boolean isValidAccount(Context context, Account account) {
        List<MessageResponse> errors = new ArrayList<>();
        // Check for valid UUID
        try {
            UUID.fromString(account.uuid);
        } catch (IllegalArgumentException e) {
            context.status(400).result(response("Bad Request", "Invalid UUID"));
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

    // TODO Implement
    @Route(path = "/user/:uuid", method = "BEFORE")
    public static Handler overrideAccount_AuthCheck = ctx -> {
        ctx.status(501);
    };

    // TODO Implement
    @OpenApi(
            summary = "Overrides the given user information with the provided information",
            description = "Override a user's account with the provided information",
            tags = {"User"},
            pathParams = {@OpenApiParam(name = "uuid", description = "UUID of the a given account", required = true)},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Account.class)}, required = true, description = "Amount information used to update the requested account"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Account.class)}, description = "Account has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "User does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user/:uuid", method = "PUT", roles = {RestRoles.DEV})
    public static Handler overrideAccount = ctx -> {
        ctx.status(501);
    };

    // TODO Implement
    @Route(path = "/user/:uuid/:data", method = "BEFORE")
    public static Handler patchAccount_AuthCheck = ctx -> {
        ctx.status(501);
    };

    // TODO Implement
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
        ctx.status(501);
    };

    // TODO Implement
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
        // Validate UUID
        String uuid = ctx.pathParam("uuid", String.class).get();
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(response("Bad Request", "PathParam UUID is not valid"));
            return;
        }
        Account account = SQLCacheAccount.getAccount(uuid);
        if (account != null)
            ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, account)));
        else
            ctx.status(404).result(response("Account Not Found", "Account with uuid " + uuid + " does not exist!"));
    };

    private static Account filterBasedOnPerms(Context ctx, Account account) {
        RestRoles role = EndpointSecurity.getRole(ctx);
        if (role.equals(RestRoles.DEV))
            return account;
        if (role.equals(RestRoles.SERVER)) {
            account.passwordHash = null;
            account.passwordSalt = null;
            account.systemPerms = null;
            return account;
        }
        if (role.equals(RestRoles.USER)) {
            // TODO Based on SystemPerms
        }
        account.discordID = null;
        account.perms = null;
        account.perks = null;
        account.muteTime = null;
        account.trackedTime = null;
        account.wallet = null;
        account.rewardPoints = null;
        account.passwordHash = null;
        account.passwordSalt = null;
        account.systemPerms = null;
        return account;
    }

    // TODO Implement
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
        ctx.status(501);
    };


    // TODO Implement
    @Route(path = "/user/:uuid/:data", method = "BEFORE")
    public static Handler getAccountInformation_AuthCheck = ctx -> {
        ctx.status(501);
    };

    // TODO Implement
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
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "/user", method = "GET")
    public static Handler getAccounts = ctx -> {
        ctx.status(501);
    };

    // TODO Implement
    @Route(path = "/user/:uuid", method = "BEFORE")
    public static Handler deleteAccount_AuthCheck = ctx -> {
        ctx.status(501);
    };

    // TODO Implement
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
        // Validate UUID
        String uuid = ctx.pathParam("uuid", String.class).get();
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(response("Bad Request", "PathParam UUID is not valid"));
            return;
        }
        // Check if account exists
        Account existingAccount = SQLCacheAccount.getAccount(uuid);
        if (existingAccount != null) {
            if (SQLCacheAccount.deleteAccount(uuid))
                ctx.status(200).result(GSON.toJson(existingAccount));
        } else
            ctx.status(404).result(response("No Account", "No Account exists with the provided UUID"));
    };
}
