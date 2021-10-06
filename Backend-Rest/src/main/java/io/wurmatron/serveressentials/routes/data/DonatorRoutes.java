package io.wurmatron.serveressentials.routes.data;

import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.Donator;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheDonator;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class DonatorRoutes {

    @OpenApi(
            summary = "Create a new donation entry",
            description = "Create a new donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Donator.class)}, required = true, description = "Information about the donation"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = Donator.class)}, description = "Donation Entry has been created successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/donator", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {
        try {
            Donator newDonator = GSON.fromJson(ctx.body(), Donator.class);
            if (isValidDonator(ctx, newDonator)) {
                Donator donator = SQLCacheDonator.newDonator(newDonator);
                ctx.status(201).result(GSON.toJson(donator));
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Cannot parse body into Donator"));
        }
    };

    @OpenApi(
            summary = "Get a list of donation entries, based on the specified query data / filters",
            description = "Create a new donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            queryParams = {
                    @OpenApiParam(name = "store", description = "Name of the store used in the entry"),
                    @OpenApiParam(name = "amount", description = "amount of money used to purchase the entry", type = Double.class),
                    @OpenApiParam(name = "uuid", description = "UUID of the amount that purchased this entry"),
                    @OpenApiParam(name = "type", description = "type of the entry"),
                    @OpenApiParam(name = "transaction", description = "ID of the transaction for the entry"),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Donator[].class)}, description = "Donation Entries that match the query"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/donator", method = "GET")
    public static Handler get = ctx -> {
        String sql = createSQLForDonatorWithFilters(ctx);
        // Send Request and Process
        List<Donator> donators = SQLDirect.queryArray(sql, new Donator());
        ctx.status(200).result(GSON.toJson(donators.toArray(new Donator[0])));
    };

    @OpenApi(
            summary = "Update an existing donation entry",
            description = "Update an existing donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Donator.class)}, required = true, description = "Updated Information about the donation"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Donator.class)}, description = "Donation Entry has been updated successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/donator", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {
        try {
            Donator overrideDonator = GSON.fromJson(ctx.body(), Donator.class);
            if (isValidDonator(ctx, overrideDonator)) {
                SQLCacheDonator.updateDonator(overrideDonator, SQLCacheDonator.getColumns());
                Donator donator = SQLCacheDonator.getDonator(overrideDonator.uuid);
                ctx.status(200).result(GSON.toJson(donator));
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Cannot parse body into Donator"));
        }
    };

    @OpenApi(
            summary = "Delete an existing donation entry",
            description = "Update an existing donation entry",
            tags = {"Donator"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = Donator.class)}, required = true, description = "Donation entry to be deleted"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Donator.class)}, description = "Donation Entry has been updated deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/donator", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {
        try {
            Donator donator = GSON.fromJson(ctx.body(), Donator.class);
            Donator cacheDonator = SQLCacheDonator.getDonator(donator.uuid);
            if (cacheDonator != null) {
                SQLCacheDonator.deleteDonator(donator.uuid);
                ctx.status(200).result(GSON.toJson(cacheDonator));
            } else
                ctx.status(404).result(response("Not Found", "Donator not found!"));
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Cannot parse body into Donator"));
        }
    };

    /**
     * Validates the donator
     *
     * @param ctx     context of the message, (used for error handling)
     * @param donator instance of the donator to verify
     * @return if the donator instance is valid or not
     */
    public static boolean isValidDonator(Context ctx, Donator donator) {
        List<MessageResponse> errors = new ArrayList<>();
        // Check store
        if (donator.store == null || donator.store.trim().isEmpty())
            errors.add(new MessageResponse("Invalid Store", "Store must be non-null and have a length() > 0"));
        // Check TransactionID
        if (donator.transaction_id == null || donator.transaction_id.trim().isEmpty())
            errors.add(new MessageResponse("Invalid TransactionID", "Transaction ID must be non-null and have a length"));
        // Check amount
        if (donator.amount == null || donator.amount < 0)
            errors.add(new MessageResponse("Invalid Amount", "Donator amount must be equal or greater than 0"));
        // Check UUID
        if (donator.uuid == null || donator.uuid.trim().isEmpty())
            errors.add(new MessageResponse("Invalid UUID", "UUID must be a valid non-empty UUID"));
        try {
            if (donator.uuid != null && !donator.uuid.trim().isEmpty())
                UUID.fromString(donator.uuid);
        } catch (Exception e) {
            errors.add(new MessageResponse("Invalid UUID", "UUID must be a valid non-empty UUID"));
        }
        if (errors.size() == 0)
            return true;
        ctx.status(400).result(GSON.toJson(errors.toArray(new MessageResponse[0])));
        return false;
    }

    /**
     * Generates a SQL Statement for get users with filters applied
     *
     * @param ctx context to get the information from the user
     * @return sql statement for currency lookup
     */
    private static String createSQLForDonatorWithFilters(Context ctx) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM " + SQLCacheDonator.DONATOR_TABLE + " WHERE ");
        // Verify, Check and Apply Store Filter
        String store = ctx.queryParam("store");
        if (store != null && !store.trim().isEmpty())
            sqlBuilder.append("store LIKE '").append(store).append("%' AND ");
        // Verify, Check and Apply UUID Filter
        String uuid = ctx.queryParam("uuid");
        if (uuid != null && !uuid.trim().isEmpty())
            sqlBuilder.append("uuid LIKE '").append(uuid).append("%' AND ");
        // Verify, Check and Apply Type Filter
        String type = ctx.queryParam("type");
        if (type != null && !type.trim().isEmpty())
            sqlBuilder.append("type LIKE '").append(type).append("%' AND ");
        // Verify, Check and Apply TransactionID Filter
        String transactionID = ctx.queryParam("transaction");
        if (transactionID != null && !transactionID.trim().isEmpty())
            sqlBuilder.append("transactionID LIKE '").append(transactionID).append("%' AND ");
        // Verify, Check and Apply Amount Filter
        String amount = ctx.queryParam("amount");
        if (amount != null && !amount.trim().isEmpty())
            sqlBuilder.append("amount=").append(amount).append(" AND ");
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
