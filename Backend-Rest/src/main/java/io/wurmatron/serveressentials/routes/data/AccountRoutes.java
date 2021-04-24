package io.wurmatron.serveressentials.routes.data;

import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.RestRoles;
import io.wurmatron.serveressentials.routes.Route;

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
        ctx.status(501);
    };

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
        ctx.status(501);
    };

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
        ctx.status(501);
    };
}
