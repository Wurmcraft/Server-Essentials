package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.sql.SQLCache;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SQLCacheRank extends SQLCache {

    /**
     * Get a rank based on its id
     *
     * @param id id for the given rank
     * @return instance of the rank
     */
    @Nullable
    public static Rank getID(long id) {
        return null;
    }

    /**
     * Get a rank based on its name
     *
     * @param name name of the given rank
     * @return instance of the rank
     */
    @Nullable
    public static Rank getName(String name) {
        return null;
    }

    /**
     * Gets a list of all the ranks
     *
     * @return a list of all the ranks
     */
    public static List<Rank> gets() {
        return new ArrayList<>();
    }

    /**
     * Creates a new rank
     *
     * @param rank instance to be created
     * @return instance that was created, with id
     */
    public static Rank create(Rank rank) {
        return rank;
    }

    /**
     * Updates a existing rank
     *
     * @param rank updated rank instance
     * @return if the rank has been updated or not
     */
    public static boolean update(Rank rank) {
        return false;
    }

    /**
     * Deletes the given rank
     *
     * @param rankID ID of the rank to be deleted
     * @return if the rank has been deleted or not
     */
    public static boolean delete(long rankID) {
        return false;
    }

    /**
     * @param rankID Id of the rank to remove from the cache
     */
    public static void invalidate(long rankID) {

    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    public static void cleanupCache() {

    }

    /**
     * Removes the expired entries from the database
     */
    public static void cleanupDB() {
    }
}
