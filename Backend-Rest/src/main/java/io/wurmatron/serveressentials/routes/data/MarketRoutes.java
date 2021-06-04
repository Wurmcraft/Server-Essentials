package io.wurmatron.serveressentials.routes.data;


import com.google.gson.JsonParseException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.wurmatron.serveressentials.models.MarketEntry;
import io.wurmatron.serveressentials.models.MessageResponse;
import io.wurmatron.serveressentials.routes.Route;
import io.wurmatron.serveressentials.sql.routes.SQLCacheMarket;
import io.wurmatron.serveressentials.sql.routes.SQLDirect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.routes.RouteUtils.response;

public class MarketRoutes {

    @OpenApi(
            summary = "Create a new market entry with the provided information",
            description = "Create a new market entry with the provided information",
            tags = {"Market"},
            headers = {@OpenApiParam(name = "Authorization", description = "Authorization Token to used for authentication within the rest API", required = true)},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = MarketEntry.class)}, required = true, description = "Market Entry information used to create the new entry"),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = MarketEntry.class)}, description = "Market Entry has been created successfully"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/market", method = "POST", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler create = ctx -> {
        try {
            MarketEntry newEntry = GSON.fromJson(ctx.body(), MarketEntry.class);
            if (isValidMarketEntry(ctx, newEntry)) {
                MarketEntry entry = SQLCacheMarket.create(newEntry);
                ctx.status(201).result(GSON.toJson(entry));
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Failed to parse body into MarketEntry"));
        }
    };

    @OpenApi(
            summary = "Get a array / list of market entries that are based on your query parameters",
            description = "Get a array / list of market entries that are based on your query parameters",
            tags = {"Market"},
            queryParams = {
                    @OpenApiParam(name = "server-id", description = "ID of the server that the market entry was created on"),
                    @OpenApiParam(name = "uuid", description = "UUID of the seller that created the market entry"),
                    @OpenApiParam(name = "market-type", description = "Type of market the entry was created within"),
                    @OpenApiParam(name = "transfer-id", description = "ID of the transfer system the market entry was created within, empty is considered global"),
                    @OpenApiParam(name = "item", description = "Item that this market entry is dealing with"),
            },
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MarketEntry[].class)}, description = "Requested Market Entries are returned"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/market", method = "GET")
    public static Handler get = ctx -> {
        String sql = createSQLForMarketWithFilters(ctx);
        // Send Request and Process
        List<MarketEntry> entries = SQLDirect.queryArray(sql, new MarketEntry());
        ctx.status(200).result(GSON.toJson(entries.toArray(new MarketEntry[0])));
    };

    @OpenApi(
            summary = "Override / Update an existing market entry",
            description = "Override / Update an existing market entry",
            tags = {"Market"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = MarketEntry.class)}, required = true, description = "Market Entry information used to update the existing entry"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MarketEntry.class)}, description = "Market Entry has been updated"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to find existing market entry, does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/market", method = "PUT", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler override = ctx -> {
        try {
            MarketEntry updateEntry = GSON.fromJson(ctx.body(), MarketEntry.class);
            if (isValidMarketEntry(ctx, updateEntry)) {
                SQLCacheMarket.update(updateEntry, new String[]{"item", "currencyName", "currencyAmount", "marketData", "transferID"});
                List<MarketEntry> entries = SQLCacheMarket.get(updateEntry.serverID, updateEntry.sellerUUID);
                for (MarketEntry entry : entries)
                    if (entry.timestamp.equals(updateEntry.timestamp)) {
                        ctx.status(200).result(GSON.toJson(entry));
                        return;
                    }
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Failed to parse body into MarketEntry"));
        }
    };

    @OpenApi(
            summary = "Delete an existing market entry",
            description = "Delete an existing market entry",
            tags = {"Market"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = MarketEntry.class)}, required = true, description = "Market Entry that you want to delete"),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = MarketEntry.class)}, description = "Market Entry has been deleted"),
                    @OpenApiResponse(status = "400", content = {@OpenApiContent(from = MessageResponse[].class)}, description = "One or more of the provided values, has failed to validate!"),
                    @OpenApiResponse(status = "401", content = {@OpenApiContent(from = MessageResponse.class)}, description = "You are missing an authorization token"),
                    @OpenApiResponse(status = "403", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Forbidden, Your provided auth token does not have permission to do this"),
                    @OpenApiResponse(status = "404", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to find existing market entry, does not exist"),
                    @OpenApiResponse(status = "422", content = {@OpenApiContent(from = MessageResponse.class)}, description = "Unable to process, due to invalid format / json"),
                    @OpenApiResponse(status = "500", content = {@OpenApiContent(from = MessageResponse.class)}, description = "The server has encountered an error, please contact the server's admin to check the logs")
            }
    )
    @Route(path = "api/market", method = "DELETE", roles = {Route.RestRoles.USER, Route.RestRoles.SERVER, Route.RestRoles.DEV})
    public static Handler delete = ctx -> {
        try {
            MarketEntry entryToDelete = GSON.fromJson(ctx.body(), MarketEntry.class);
            if (isValidMarketEntry(ctx, entryToDelete)) {
                List<MarketEntry> entries = SQLCacheMarket.get(entryToDelete.serverID, entryToDelete.sellerUUID);
                for (MarketEntry entry : entries)
                    if (entry.timestamp.equals(entryToDelete.timestamp)) {
                        SQLCacheMarket.delete(entryToDelete.serverID, entryToDelete.sellerUUID, entryToDelete.timestamp);
                        ctx.status(200).result(GSON.toJson(entry));
                        return;
                    }
            }
        } catch (JsonParseException e) {
            ctx.status(422).result(response("Invalid JSON", "Failed to parse body into MarketEntry"));
        }
    };

    public static boolean isValidMarketEntry(Context ctx, MarketEntry entry) {
        List<MessageResponse> errors = new ArrayList<>();
        // Verify ServerID
        if (entry.serverID == null && entry.serverID.trim().isEmpty())
            errors.add(new MessageResponse("Invalid ServerID", "ServerID must be non-null"));
        // Verify CurrencyName
        if (entry.currencyName == null && entry.currencyName.trim().isEmpty())
            errors.add(new MessageResponse("Invalid Currency", "Currency-Name must be non-null"));
        // Verify MarketType
        if (entry.marketType == null && entry.marketType.trim().isEmpty())
            errors.add(new MessageResponse("Invalid Market Type", "Market Type must be non-null"));
        // Check Currency Amount
        if (entry.currencyAmount < 0)
            errors.add(new MessageResponse("Invalid Currency Amount", "Amount must be greater or equal to 0"));
        if (entry.sellerUUID == null || entry.sellerUUID.trim().isEmpty())
            errors.add(new MessageResponse("Invalid UUID", "UUID must be a valid UUID"));
        try {
            if (entry.sellerUUID != null)
                UUID.fromString(entry.sellerUUID);
        } catch (Exception e) {
            errors.add(new MessageResponse("Invalid UUID", "UUID must be a valid UUID"));
        }
        if (entry.item == null || entry.item.item == null || entry.item.item.trim().isEmpty())
            errors.add(new MessageResponse("Invalid Item", "Item must be non-null"));
        if (entry.item == null && entry.item.count > 0)
            errors.add(new MessageResponse("Invalid Item", "Item Count must be greater than 0"));
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
    private static String createSQLForMarketWithFilters(Context ctx) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM " + SQLCacheMarket.MARKET_TABLE + " WHERE ");
        // Verify, Check and Apply ServerID Filter
        String serverID = ctx.queryParam("server-id");
        if (serverID != null && !serverID.trim().isEmpty())
            sqlBuilder.append("serverID LIKE '").append(serverID).append("%' AND ");
        // Verify, Check and Apply UUID Filter
        String uuid = ctx.queryParam("uuid");
        if (uuid != null && !uuid.trim().isEmpty())
            sqlBuilder.append("sellerUUID LIKE '").append(uuid).append("%' AND ");
        // Verify, Check and Apply MarketType Filter
        String marketType = ctx.queryParam("market-type");
        if (marketType != null && !marketType.trim().isEmpty())
            sqlBuilder.append("marketType LIKE '").append(marketType).append("%' AND ");
        // Verify, Check and Apply TransferID Filter
        String transferID = ctx.queryParam("transfer-id");
        if (transferID != null && !transferID.trim().isEmpty())
            sqlBuilder.append("transferID LIKE '").append(transferID).append("%' AND ");
        // Verify, Check and Apply Item Filter
        String item = ctx.queryParam("item");
        if (item != null && !item.trim().isEmpty())
            sqlBuilder.append("item LIKE '").append(item).append("%' AND ");
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
