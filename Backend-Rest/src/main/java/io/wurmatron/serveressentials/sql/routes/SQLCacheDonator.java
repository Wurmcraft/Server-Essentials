package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Donator;
import io.wurmatron.serveressentials.sql.SQLCache;

import javax.annotation.Nullable;

public class SQLCacheDonator extends SQLCache {

    public static final String DONATOR_TABLE = "donator";

    /**
     * Get a donator based on there UUID
     *
     * @param uuid uuid of the donator
     * @return instance of the donator
     */
    @Nullable
    public static Donator getDonator(String uuid) {
        return null;
    }


    /**
     * Creates a new donator
     *
     * @param donator instance of the donator to be created
     * @return donator instance will all information filled out
     */
    public static Donator newDonator(Donator donator) {
        return null;
    }

    /**
     * Updates specific donator information
     *
     * @param donator         instance of donator to pull the updated rows from
     * @param columnsToUpdate values to update in the SQL
     * @return if the donator has been successfully updated
     */
    public static boolean updateDonator(Donator donator, String[] columnsToUpdate) {
        return false;
    }

    /**
     *  Delete a specific donator based on there uuid
     *
     * @param uuid uuid of the donator to be deleted
     * @return if the given uuid donator has been deleted
     */
    public static boolean deleteDonator(String uuid) {
        return false;
    }

    public static void invalidate(String uuid) {

    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    public static void cleanupCache() {

    }

    /**
     * This should do nothing, its here to prevent an possible reflection issue
     */
    public static void cleanupDB() {
    }
}
