package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Donator;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheDonator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class SQLCacheDonator extends SQLCache {

    public static final String DONATOR_TABLE = "donator";

    /**
     * Get a donator based on there UUID
     *
     * @param uuid uuid of the donator
     * @return instance of the donator
     * @see SQLGenerator#get(String, String, String, String, Object)
     * @see #invalidate(String)
     */
    @Nullable
    public static Donator getDonator(String uuid) {
        // Attempt to get from cache
        if (donatorCache.containsKey(uuid))
            if (!needsUpdate(donatorCache.get(uuid)))
                return donatorCache.get(uuid).donator;
            else
                donatorCache.remove(uuid);
        // Not in cache / invalid
        try {
            Donator donator = get("*", DONATOR_TABLE, "uuid", uuid, new Donator());
            if (donator != null) {
                donatorCache.put(uuid, new CacheDonator(donator));
                return donator;
            }
        } catch (Exception e) {
            LOG.debug("Failed to find donator with uuid ' " + uuid + "' (" + e.getMessage() + ")");
        }
        // Donator does not exist
        return null;
    }


    /**
     * Creates a new donator
     *
     * @param donator instance of the donator to be created
     * @return donator instance will all information filled out
     * @see SQLGenerator#insert(String, String[], Object, boolean)
     */
    public static Donator newDonator(Donator donator) {
        try {
            insert(DONATOR_TABLE, DONATOR_COLUMNS, donator, false);
            donatorCache.put(donator.uuid, new CacheDonator(donator));
            return donator;
        } catch (Exception e) {
            LOG.debug("Failed to add donator with uuid '" + donator.uuid + "' (" + e.getMessage() + ")");
            LOG.debug("Donator: " + GSON.toJson(donator));
        }
        return null;
    }

    /**
     * Updates specific donator information
     *
     * @param donator         instance of donator to pull the updated rows from
     * @param columnsToUpdate values to update in the SQL
     * @return if the donator has been successfully updated
     * @see SQLGenerator#update(String, String[], String, String, Object)
     */
    public static boolean updateDonator(Donator donator, String[] columnsToUpdate) {
        try {
            update(DONATOR_TABLE, columnsToUpdate, "uuid", donator.uuid, donator);
            if (donatorCache.containsKey(donator.uuid)) {    // Exists in cache, updating
                donatorCache.get(donator.uuid).donator = updateInfoLocal(columnsToUpdate, donator, donatorCache.get(donator.uuid).donator);
                donatorCache.get(donator.uuid).lastSync = System.currentTimeMillis();
            } else {    // Missing from cache
                donatorCache.put(donator.uuid, new CacheDonator(donator));
            }
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to update donator with uuid '" + donator.uuid + "' (" + e.getMessage() + ")");
            LOG.debug("Donator: " + GSON.toJson(donator));
        }
        return false;
    }

    /**
     * Delete a specific donator based on there uuid
     *
     * @param uuid uuid of the donator to be deleted
     * @return if the given uuid donator has been deleted
     * @see #invalidate(String)
     */
    public static boolean deleteDonator(String uuid) {
        try {
            delete(DONATOR_TABLE, "uuid", uuid);
            invalidate(uuid);
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to delete donator with uuid '" + uuid + "' (" + e.getMessage() + ")");
        }
        return false;
    }

    /**
     * Removes a entry from the cache, causing an update upon next request
     * Note: Does not delete anything from the database
     *
     * @param uuid id used for the account to remove from cache
     */
    public static void invalidate(String uuid) {
        donatorCache.remove(uuid);
        LOG.debug("Donator '" + uuid + " has been invalidated, will update on next request!");
    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    public static void cleanupCache() {
        LOG.info("Donator Cache cleanup has begun!");
        // ID Cache
        List<String> toBeRemoved = new ArrayList<>();
        for(CacheDonator entry : donatorCache.values())
            if(needsUpdate(entry))
                toBeRemoved.add(entry.donator.uuid);
            // Remove from Cache
        int count = 0;
        for(String donatorEntry : toBeRemoved) {
            count++;
            invalidate(donatorEntry);
        }
        LOG.info("Donator Cache has been cleaned, " + count + " entries have been removed!");
    }

    /**
     * This should do nothing, its here to prevent an possible reflection issue
     */
    public static void cleanupDB() {
    }
}
