package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.MarketEntry;
import io.wurmatron.serveressentials.sql.SQLCache;

import javax.annotation.Nullable;
import java.util.List;

public class SQLCacheMarket extends SQLCache {

    public static final String MARKET_TABLE = "markets";

    /**
     *
     * @param serverID id of the server the market entry exists on
     * @param sellerUUID uuid of the server that created this entry
     * @return instance of all the market entries related to the server and seller
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String, Object) (String, String, String, String, Object)
     */
    // TODO Implement
    public static List<MarketEntry> get(String serverID, String sellerUUID) {
        return null;
    }

    /**
     * Returns a list of all the market entries instances within the db, that are from the requested server, if any exist
     *
     * @param serverID id of the server to look for market entries
     * @return instance of all the market entries related to the provided server
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String, Object)
     */
    // TODO Implement
    public static List<MarketEntry> getServer(String serverID) {
        return null;
    }

    /**
     * Returns a list of all the market entries instances within the db that are of the provided type
     *
     * @return instance of all the market entries in the db, that are of this type, if any exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String, Object)
     */
    // TODO Implement
    public static List<MarketEntry> get(String marketType) {
        return null;
    }

    /**
     * Creates a new market entry in the db
     *
     * @param entry instance of the market entry to be created
     * @return returns the same object, that was provided
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object, boolean)
     */
    // TODO Implement
    @Nullable
    public static MarketEntry create(MarketEntry entry) {
        return null;
    }

    /**
     * Updates a market entry in the db
     *
     * @param entry market entry to be updated
     * @return if the update was successful
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#update(String, String[], String, String, Object)
     */
    // TODO Implement
    public static boolean update(MarketEntry entry, String[] columnsToUpdate) {
        return false;
    }

    /**
     * Deletes / Removes a Market Entry from the db
     *
     * @param serverID  serverID of the given market entry
     * @param uuid      uuid of the user that created the given entry
     * @param timestamp timestamp when the entry was created
     * @return if the given entry  was successfully deleted
     */
    public static boolean delete(String serverID, String uuid, long timestamp) {
        return false;
    }

    /**
     * Invalidates / removes the the given entries
     *
     * @param uuid name of the user that related to the entry's
     */
    // TODO Implement
    public static void invalidate(String uuid) {

    }

    /**
     * Invalidates / removes the given entries
     *
     * @param serverID id of the server that this entry is part of
     * @param uuid     uuid of the user this entry belongs to
     */
    // TODO Implement
    public static void invalidate(String serverID, String uuid) {

    }

    /**
     * Run periodically to cleanup the cache and remove expired / invalid entries
     */
    // TODO Implement
    public static void cleanupCache() {

    }


    /**
     * Run periodically to cleanup the db and remove expired / invalid entries
     */
    // TODO Implement
    public static void cleanupDB() {

    }
}
