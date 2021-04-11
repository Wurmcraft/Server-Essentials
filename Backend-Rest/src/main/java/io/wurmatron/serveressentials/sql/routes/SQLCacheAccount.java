package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cacheHolder.CacheAccount;

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
     * @see SQLGenerator#insert(String, String[], Object)
     */
    public static void newAccount(Account account) {
        try {
            insert(USERS_TABLE, USERS_COLUMNS, account);
            accountCache.put(account.uuid, new CacheAccount(account));
        } catch (Exception e) {
            LOG.debug("Failed to add account with uuid '" + account.uuid + "' (" + e.getMessage() + ")");
            LOG.debug("Account: " + GSON.toJson(account));
        }
    }

    /**
     * Updates a existing account with the provided info
     *
     * @param account         account to collect the data to be updated
     * @param columnsToUpdate columns in the database to update with the provided data
     * @see SQLGenerator#update(String, String[], String, String, Object)
     */
    public static void updateAccount(Account account, String[] columnsToUpdate) {
        try {
            update(USERS_TABLE, columnsToUpdate, "uuid", account.uuid, account);
            if (accountCache.containsKey(account.uuid)) {    // Exists in cache, updating
                try {
                    accountCache.get(account.uuid).account = updateInfoLocal(columnsToUpdate, account, accountCache.get(account.uuid).account);
                    accountCache.get(account.uuid).lastSync = System.currentTimeMillis();
                } catch (Exception e) {
                    LOG.debug("");
                }
            } else {    // Missing from cache
                accountCache.put(account.uuid, new CacheAccount(account));
            }
        } catch (Exception e) {
            LOG.debug("Failed to update account with uuid '" + account.uuid + "' (" + e.getMessage() + ")");
            LOG.debug("Account: " + GSON.toJson(account));
        }
    }


    /**
     * Deletes the given account from the database
     * To remove from cache, but not delete use invalidate(uuid)
     *
     * @param uuid uuid of the account to be deleted
     * @see #invalidate(String)
     */
    public static void deleteAccount(String uuid) {
        try {
            delete(USERS_TABLE, "uuid", uuid);
        } catch (Exception e) {
            LOG.debug("Failed to delete account with uuid '" + uuid + "' (" + e.getMessage() + ")");
        }
    }

    public static void invalidate(String uuid) {
        accountCache.remove(uuid);
        LOG.debug("Account '" + uuid + " has been invalidated, will update on next request!");
    }
}