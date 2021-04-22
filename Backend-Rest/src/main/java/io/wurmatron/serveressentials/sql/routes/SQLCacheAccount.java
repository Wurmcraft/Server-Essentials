package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheAccount;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

/**
 * Interact with the Account's part of the database, with caching
 */
public class SQLCacheAccount extends SQLCache {

    public static final String USERS_TABLE = "users";

    /**
     * Gets the user's data from the cache, if not requests from database
     * Note: If the data accuracy is important, consider invalidating the uuid before calling this, this will force an update
     *
     * @param uuid uuid of the account to lookup
     * @return instance of the account, based on the uuid
     * @see SQLGenerator#get(String, String, String, String, Object)
     * @see #invalidate(String)
     */
    @Nullable
    public static Account getAccount(String uuid) {
        // Attempt to get from cache
        if (accountCache.containsKey(uuid))
            if (!needsUpdate(accountCache.get(uuid))) {
                return accountCache.get(uuid).account;
            } else {
                accountCache.remove(uuid);
            }
        // Not in cache / invalid
        try {
            Account account = get("*", USERS_TABLE, "uuid", uuid, new Account());
            if (account != null) {
                accountCache.put(uuid, new CacheAccount(account));
                return account;
            }
        } catch (Exception e) {
            LOG.debug("Failed to find account with uuid '" + uuid + "' (" + e.getMessage() + ")");
        }
        // User does not exist
        return null;
    }

    /**
     * Creates a new account with the provided instance
     *
     * @param account instance of the account to be created
     * @see SQLGenerator#insert(String, String[], Object, boolean)
     */
    @Nullable
    public static Account newAccount(Account account) {
        try {
            insert(USERS_TABLE, USERS_COLUMNS, account,false);
            accountCache.put(account.uuid, new CacheAccount(account));
            return account;
        } catch (Exception e) {
            LOG.debug("Failed to add account with uuid '" + account.uuid + "' (" + e.getMessage() + ")");
            LOG.debug("Account: " + GSON.toJson(account));
        }
        return null;
    }

    /**
     * Updates a existing account with the provided info
     *
     * @param account         account to collect the data to be updated
     * @param columnsToUpdate columns in the database to update with the provided data
     * @see SQLGenerator#update(String, String[], String, String, Object)
     */
    public static boolean updateAccount(Account account, String[] columnsToUpdate) {
        try {
            update(USERS_TABLE, columnsToUpdate, "uuid", account.uuid, account);
            if (accountCache.containsKey(account.uuid)) {    // Exists in cache, updating
                try {
                    accountCache.get(account.uuid).account = updateInfoLocal(columnsToUpdate, account, accountCache.get(account.uuid).account);
                    accountCache.get(account.uuid).lastSync = System.currentTimeMillis();
                } catch (Exception e) {
                }
            } else {    // Missing from cache
                accountCache.put(account.uuid, new CacheAccount(account));
            }
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to update account with uuid '" + account.uuid + "' (" + e.getMessage() + ")");
            LOG.debug("Account: " + GSON.toJson(account));
        }
        return false;
    }


    /**
     * Deletes the given account from the database
     * To remove from cache, but not delete use invalidate(uuid)
     *
     * @param uuid uuid of the account to be deleted
     * @see #invalidate(String)
     */
    public static boolean deleteAccount(String uuid) {
        try {
            delete(USERS_TABLE, "uuid", uuid);
            invalidate(uuid);
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to delete account with uuid '" + uuid + "' (" + e.getMessage() + ")");
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
        accountCache.remove(uuid);
        LOG.debug("Account '" + uuid + " has been invalidated, will update on next request!");
    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    public static void cleanupCache() {
        LOG.info("Account Cache cleanup has begun!");
        // ID Cache
        List<String> toBeRemoved = new ArrayList<>();
        for (CacheAccount entry : accountCache.values())
            if (needsUpdate(entry))
                toBeRemoved.add(entry.account.uuid + "");
        // Remove from cache
        int count = 0;
        for (String accountEntry : toBeRemoved) {
            count++;
            invalidate(accountEntry);
        }
        LOG.info("Account Cache has been cleaned, " + count + " entries have been removed!");
    }

    /**
     * This should do nothing, its here to prevent an possible reflection issue
     */
    public static void cleanupDB() {
    }
}