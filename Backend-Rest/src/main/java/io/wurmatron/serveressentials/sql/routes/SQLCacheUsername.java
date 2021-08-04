package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheUsername;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.sql.routes.SQLCacheAccount.USERS_TABLE;

public class SQLCacheUsername extends SQLCache {

    private static final NonBlockingHashMap<String, CacheUsername> usernameCache = new NonBlockingHashMap<>();

    /**
     * Gets the user's username via uuid data from the cache, if not requests from database
     * Note: If the data accuracy is important, consider invalidating the uuid before calling this, this will force an update
     *
     * @param uuid uuid of the username to lookup
     * @return username, based on the uuid
     * @see SQLGenerator#get(String, String, String, String, Object)
     * @see #invalidate(String)
     */
    @Nullable
    public static String get(String uuid) {
        // Attempt to get from cache
        if (usernameCache.containsKey(uuid))
            if (!needsUpdate(usernameCache.get(uuid)))
                return usernameCache.get(uuid).username;
            else
                accountCache.remove(uuid);
        // Not in cache / invalid
        try {
            Account username = get("*", USERS_TABLE, "uuid", uuid, new Account());
            if (username != null) {
                usernameCache.put(uuid, new CacheUsername(username.username));
                return username.username;
            }
        } catch (Exception e) {
            LOG.debug("Failed to find account username with uuid '" + uuid + "' (" + e.getMessage() + ")");
        }
        // uuid does not exist
        return null;
    }

    /**
     * Removes a entry from the cache, causing an update upon next request
     * Note: Does not delete anything from the database
     *
     * @param uuid uuid of the username to remove from the cache
     */
    public static void invalidate(String uuid) {
        usernameCache.remove(uuid);
        LOG.debug("Username Entry '" + uuid + " has been invalidated, will update on next request!");
    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    public static void cleanupCache() {
        LOG.info("Username Cache cleanup has begun!");
        List<String> toBeRemoved = new ArrayList<>();
        for (String uuid : usernameCache.keySet())
            if (needsUpdate(usernameCache.get(uuid)))
                toBeRemoved.add(uuid);
        // Remove from Cache
        int count = 0;
        for (String uuid : toBeRemoved)
            SQLCacheAccount.invalidate(uuid);
        count++;
        LOG.info("Username Cache has been cleaned, " + count + " entries have been removed!");
    }

    /**
     * Removes the expired entries from the database
     */
    public static void cleanupDB() {
    }
}
