package io.wurmatron.serveressentials.sql;

import io.wurmatron.serveressentials.sql.cacheHolder.*;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAccount;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.lang.reflect.Field;

import static io.wurmatron.serveressentials.ServerEssentialsRest.config;

/**
 * Between direct sql and the caching mechanism
 *
 * @see SQLCacheAccount Account Caching
 * @see SQLGenerator    Direct SQL
 */
public class SQLCache extends SQLGenerator {

    // Cache (applicably only)
    protected static final NonBlockingHashMap<String, CacheAutoRank> autoRankCache = new NonBlockingHashMap<>();
    protected static final NonBlockingHashMap<String, CacheBan> bansCache = new NonBlockingHashMap<>();
    protected static final NonBlockingHashMap<String, CacheCurrency> currencyCache = new NonBlockingHashMap<>();
    protected static final NonBlockingHashMap<String, CacheRank> donatorCache = new NonBlockingHashMap<>();
    protected static final NonBlockingHashMap<String, CacheMarket> marketCache = new NonBlockingHashMap<>();
    protected static final NonBlockingHashMap<String, CacheRank> rankCache = new NonBlockingHashMap<>();
    protected static final NonBlockingHashMap<String, CacheTransfer> transferCache = new NonBlockingHashMap<>();
    protected static final NonBlockingHashMap<String, CacheAccount> accountCache = new NonBlockingHashMap<>();

    /**
     * Checks if a cached entry has expired or not
     *
     * @param cacheData entry from the cache
     * @return if a cache entry has expired and needs updating
     */
    protected static <T extends Cache> boolean needsUpdate(T cacheData) {
        return cacheData.lastSync() + (config.server.cacheTime * 1000L) < System.currentTimeMillis();
    }

    /**
     * Mimics how a database update is completed, without the need to request the update from the database
     *
     * @param columnsToUpdate columns in the database that have been updated
     * @param updateData      data that was used to update the database
     * @param localInfo       data from the database, before the update
     * @return Updated version of the data, (should be in-sync with the database)
     * @throws NoSuchFieldException   Issue with reflection to collect data from the object instance
     * @throws IllegalAccessException Issue with reflection to collect data from the object instance
     */
    protected static <T> T updateInfoLocal(String[] columnsToUpdate, T updateData, T localInfo) throws NoSuchFieldException, IllegalAccessException {
        for (String column : columnsToUpdate) {
            Field field = updateData.getClass().getDeclaredField(column);
            field.set(localInfo, field.get(updateData));
        }
        return localInfo;
    }
}
