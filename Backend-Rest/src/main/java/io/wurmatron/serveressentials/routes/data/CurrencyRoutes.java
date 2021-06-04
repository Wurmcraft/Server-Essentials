package io.wurmatron.serveressentials.routes.data;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Currency;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.EndpointSecurity;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheCurrency;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;

import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class CurrencyRoutes {

    @OpenApi(
            summary = "Create a new currency entry",
            description = "Create a new currency entry",
            tags = {"Currency"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Currency.class)}, required = true, description = "Information about the new currency"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Currency.class)}, description = "Currency was created successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/currency", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {
        try {
            Currency newCurrency = GSON.fromJson(ctx.body(), Currency.class);
            if (isValidCurrency(ctx, newCurrency)) {
                Currency currency = SQLCacheCurrency.create(newCurrency);
                ctx.status(201).result(GSON.toJson(currency));
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Cannot parse body into Currency"));
        }
    };

    @OpenApi(
            summary = "Get an existing currency",
            description = "Get an existing currency",
            tags = {"Currency"},
            queryParams = {
                    @OpenApiParam(name = "display-name", description = "Filter based on its partial or full display name"),
                    @OpenApiParam(name = "global-worth", description = "Filter based on the currency's global worth", type = Double.class),
                    @OpenApiParam(name = "global-sell-worth", description = "Filter based on the currency's global sell worth", type = Double.class),
                    @OpenApiParam(name = "tax", description = "Filter based on the currency's tax", type = Double.class),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Currency[].class)}, description = "List of all the currencies that match the query filters"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/currency", method = "GET")
    public static Handler get = ctx -> {
        String sql = createSQLForCurrencyWithFilters(ctx);
        // Send Request and Process
        List<Currency> currencies = SQLDirect.queryArray(sql, new Currency());
        ctx.status(200).result(GSON.toJson(currencies.toArray(new Currency[0])));
    };

    @OpenApi(
            summary = "Get an existing currency",
            description = "Get an existing currency",
            tags = {"Currency"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Currency.class)}, description = "Get the currency based on its id"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/currency/:id", method = "GET")
    public static Handler getID = ctx -> {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Currency currency = SQLCacheCurrency.get(id);
            if (currency != null)
                ctx.status(200).result(GSON.toJson(filterBasedOnPerms(ctx, currency)));
            else
                ctx.status(404).result(response("Not Found", "Currency with the provided ID does not exist"));
        } catch (NumberFormatException e) {
            ctx.status(400).result(response("Bad Request", "ID must be a number, greater or equal to 0"));
        }
    };

    @OpenApi(
            summary = "Update an existing currency",
            description = "Update an existing currency",
            tags = {"Currency"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Currency.class)}, required = true, description = "Information about the new currency"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Currency.class)}, description = "Currency was updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Currency does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/currency/:id", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {
        try {
            Currency currencyUpdate = GSON.fromJson(ctx.body(), Currency.class);
            try {
                long id = Long.parseLong(ctx.pathParam("id"));
                Currency currency = SQLCacheCurrency.get(id);
                if (id != currencyUpdate.currencyID) {
                    ctx.status(400).result(response("ID Mismatch", "Path and Body ID's dont match!"));
                    return;
                }
                if (currency != null) {
                    SQLCacheCurrency.update(currencyUpdate, SQLCacheCurrency.getColumns());
                    ctx.status(200).result(GSON.toJson(SQLCacheCurrency.get(currencyUpdate.currencyID)));
                } else
                    ctx.status(404).result(response("Not Found", "Currency with the provided ID does not exist"));
            } catch (NumberFormatException e) {
                ctx.status(400).result(response("Bad Request", "ID must be a number, greater or equal to 0"));
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Cannot parse body into Currency"));
        }
    };

    @OpenApi(
            summary = "Delete an existing currency",
            description = "Delete an existing currency",
            tags = {"Currency"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Currency.class)}, required = true, description = "Information about the currency to be deleted"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Currency.class)}, description = "Currency was deleted successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Currency does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/currency/:id", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {
        try {
            long id = Long.parseLong(ctx.pathParam("id"));
            Currency currency = SQLCacheCurrency.get(id);
            if (currency != null) {
                SQLCacheCurrency.delete(id);
                ctx.status(200).result(GSON.toJson(currency));
            } else
                ctx.status(404).result(response("Not Found", "Currency with the provided ID does not exist"));
        } catch (NumberFormatException e) {
            ctx.status(400).result(response("Bad Request", "ID must be a number, greater or equal to 0"));
        }
    };

    /**
     * Validates if the provided instance is valid
     *
     * @param ctx      context of the message / request (for error handling)
     * @param currency instance of the currency to check
     * @return if the currency is valid or not
     */
    public static boolean isValidCurrency(Context ctx, Currency currency) {
        List<MessageResponse> errors = new ArrayList<>();
        // Check DisplayName
        if (currency.displayName == null || currency.displayName.trim().isEmpty())
            errors.add(new MessageResponse("Bad Name", "Display Name must not be empty / null"));
        if (currency.globalWorth <= 0)
            errors.add(new MessageResponse("Invalid Worth", "Global Worth must be greater than 0.00"));
        if (currency.sellWorth <= 0)
            errors.add(new MessageResponse("Invalid Sell", "Global Sell must be greater than 0.00"));
        if (currency.tax < 0)
            errors.add(new MessageResponse("Invalid Tax", "Tax must not be negative"));
        if (errors.size() == 0)
            return true;
        ctx.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
        return false;
    }

    /**
     * Filters / Removes data based on the users permission / authentication
     *
     * @param ctx      context to get the user from, user authentication
     * @param currency instance to remove the data from before returning
     * @return filtered version of the autoRank instance with data removed (if required)
     */
    private static Currency filterBasedOnPerms(Context ctx, Currency currency) {
        Route.RestRoles role = EndpointSecurity.getRole(ctx);
        if (role.equals(Route.RestRoles.DEV) || role.equals(Route.RestRoles.SERVER))
            return currency;
        Currency clone = currency.clone();
        if (role.equals(Route.RestRoles.USER)) {
            // TODO Based on SystemPerms
        }
        currency.globalWorth = null;
        currency.sellWorth = null;
        currency.tax = null;
        return clone;
    }

    /**
     * Generates a SQL Statement for get users with filters applied
     *
     * @param ctx context to get the information from the user
     * @return sql statement for currency lookup
     */
    private static String createSQLForCurrencyWithFilters(Context ctx) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM " + SQLCacheCurrency.CURRENCY_TABLE + " WHERE ");
        // Verify, Check and Apply DisplayName Filter
        String displayName = ctx.queryParam("display-name");
        if (displayName != null && !displayName.trim().isEmpty())
            sqlBuilder.append("displayName LIKE '").append(displayName).append("%' AND ");
        // Verify, Check and Apply Global Worth Filter
        String globalWorth = ctx.queryParam("global-worth");
        if (globalWorth != null && !globalWorth.trim().isEmpty())
            sqlBuilder.append("globalWorth=").append(globalWorth).append(" AND ");
        // Verify, Check and Apply Global Sell Filter
        String globalSell = ctx.queryParam("global-sell-worth");
        if (globalSell != null && !globalSell.trim().isEmpty())
            sqlBuilder.append("sellWorth=").append(globalSell).append(" AND ");
        // Verify, Check and Apply Tax Filter
        String tax = ctx.queryParam("tax");
        if (tax != null && !tax.trim().isEmpty())
            sqlBuilder.append("tax=").append(tax).append(" AND ");
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
