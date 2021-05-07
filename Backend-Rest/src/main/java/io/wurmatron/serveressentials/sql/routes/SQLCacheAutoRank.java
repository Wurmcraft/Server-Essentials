package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class SQLCacheAutoRank extends SQLCache {

    public static final String AUTORANK_TABLE = "autoranks";

    /**
     * Get a auto rank based on the current rank
     *
     * @param currentRank name of rank that is part of the rank up
     * @return instance of autorank
     * @see SQLGenerator#get(String, String, String, String, Object)
     * @see #invalidate(String)
     */
    // TODO Implement
    @Nullable
    public static AutoRank getName(String currentRank) {
        return null;
    }

    /**
     * Get a auto rank based on its ID
     *
     * @param autoRankID id of auto-rank
     * @return instance of autorank
     * @see SQLGenerator#get(String, String, String, String, Object)
     * @see #invalidate(String)
     */
    // TODO Implement
    @Nullable
    public static AutoRank getID(long autoRankID) {
        return null;
    }

    /**
     * Creates a new auto rank
     *
     * @param autoRank instance to be created
     * @return auto rank instance with all information filled out
     * @see SQLGenerator#insert(String, String[], Object, boolean)
     */
    // TODO Implement
    @Nullable
    public static AutoRank create(AutoRank autoRank) {
        return autoRank;
    }

    /**
     * Updates the specific auto rank information
     *
     * @param autoRank        instance of autorank to pull the updated rows from
     * @param columnsToUpdate values to update in the SQL
     * @return if the auto rank has been successfully updated
     * @see SQLGenerator#update(String, String[], String, String, Object)
     */
    // TODO Implement
    public static boolean update(AutoRank autoRank, String[] columnsToUpdate) {
        return false;
    }

    /**
     * Gets a list of all the auto-ranks
     *
     * @return a list of all the auto-ranks
     * @see SQLGenerator#getArray(String, String, String, String, Object)
     */
    // TODO Implement
    public static List<AutoRank> get() {
        return new ArrayList<>();
    }

    /**
     * Deletes a specific AutoRank based on its the users current rank
     *
     * @param autorankID id of the autorank to be deleted
     * @return if the given autorank has been deleted
     * @see #invalidate(String)
     */
    // TODO Implement
    public static boolean delete(long autorankID) {
        return false;
    }

    /**
     * Removes a entry from the cache, causing an update upon next request
     * Note: Does not delete anything from the database
     *
     * @param rank name id used for autorank to be remove from cache
     */
    public static void invalidate(String rank) {
        autoRankCache.remove(rank);
        LOG.debug("Auto-Rank '" + rank + " has been invalidated, will update on next request!");
    }

    /**
     * Removes a entry from the cache, causing an update upon next request
     * Note: Does not delete anything from the database
     *
     * @param autorankID  id of the autorank
     */
    // TODO Implement
    public static void invalidate(long autorankID) {

    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    // TODO Implement
    public static void cleanupCache() {

    }

    /**
     * This should do nothing, its here to prevent an possible reflection issue
     */
    public static void cleanupDB() {
    }
}
