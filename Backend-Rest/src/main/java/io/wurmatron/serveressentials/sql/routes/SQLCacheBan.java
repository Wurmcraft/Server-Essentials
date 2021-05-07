package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Ban;
import io.wurmatron.serveressentials.sql.SQLCache;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SQLCacheBan extends SQLCache {

    public static String BAN_TABLE = "bans";

    /**
     * Returns the given instance based on the banID, if one exists
     *
     * @param banID id of the ban
     * @return instance of the ban, from the db, if exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object)
     */
    // TODO Implement
    @Nullable
    public static Ban get(long banID) {
        return null;
    }

    /**
     * Returns the given instance based on the user uuid, if one exists
     *
     * @param uuid uuid of the banned user
     * @return instance of the ban, from the db, if exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object)
     */
    // TODO Implement
    @Nullable
    public static Ban get(String uuid) {
        return null;
    }

    /**
     * Returns a list of all the ban instances within the db
     *
     * @return instance of all the bans in the db, if any exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String, Object)
     */
    // TODO Implement
    public static List<Ban> get() {
        return new ArrayList<>();
    }

    /**
     * Creates a new ban in the db
     *
     * @param ban instance of the ban to be created
     * @return ban instance with all the data filled out, in this case 'banID' is returned
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object, boolean)
     */
    // TODO Implement
    @Nullable
    public static Ban create(Ban ban) {
        return null;
    }

    /**
     * Updates a ban entry in the db
     *
     * @param ban             instance of the ban to pull the updates from
     * @param columnsToUpdate list of the variables that need to be updated
     * @return if the update was successful
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#update(String, String[], String, String, Object)
     */
    // TODO Implement
    public static boolean update(Ban ban, String[] columnsToUpdate) {
        return false;
    }

    /**
     * Delete / Remove a ban from the db
     *
     * @param banID id of the ban to be deleted
     * @return if the ban was deleted successfully
     */
    // TODO Implement
    public static boolean delete(long banID) {
        return false;
    }

    /**
     * Invalidates / removes the given ban entry
     *
     * @param banID id if the ban to remove from the sql cache
     */
    // TODO Implement
    public static void invalidate(long banID) {

    }

    /**
     * Invalidates / removes the given user's ban entry
     *
     * @param uuid uuid of the user to remove from the sql cache
     */
    // TODO Implement
    public static void invalidate(String uuid) {

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
