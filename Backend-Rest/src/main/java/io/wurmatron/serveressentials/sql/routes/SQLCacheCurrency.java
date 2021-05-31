package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Currency;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.cache_holder.CacheCurrency;
import io.wurmatron.serveressentials.sql.cache_holder.CacheID;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class SQLCacheCurrency extends SQLCache {

    public static String CURRENCY_TABLE = "currencys";

    private static NonBlockingHashMap<String, CacheID> nameCache = new NonBlockingHashMap<>();

    /**
     * Returns the given instance based on the id, if one exists
     *
     * @param id id of the currency
     * @return instance of the currency, from the db, if exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object)
     */
    @Nullable
    public static Currency get(long id) {
        // Check if in cache
        if (currencyCache.containsKey(id)) {
            if (!needsUpdate(currencyCache.get(id)))
                return currencyCache.get(id).currency;
            else
                invalidate(id);
        }
        // Not In cache / invalid
        try {
            Currency currency = get("*", CURRENCY_TABLE, "currencyID", "" + id, new Currency());
            if (currency != null) {
                currencyCache.put(id, new CacheCurrency(currency));
                return currency.clone();
            }
        } catch (Exception e) {
            LOG.debug("Failed to GET currency for id '" + id + "' (" + e.getMessage() + ")");
        }
        return null;
    }

    /**
     * Returns the given instance based on the name, if one exists
     *
     * @param name name of the currency
     * @return instance of the currency, from the db, if exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object)
     */
    @Nullable
    public static Currency get(String name) {
        // Check for name in cache
        if (nameCache.containsKey(name.toUpperCase()))
            if (!needsUpdate(nameCache.get(name.toUpperCase())))
                return get(nameCache.get(name.toUpperCase()).id);
            else
                invalidate(name.toUpperCase());
        // Invalid / missing
        try {
            Currency currency = get("*", CURRENCY_TABLE, "displayName", name, new Currency());
            if (currency != null) {
                currencyCache.put(currency.currencyID, new CacheCurrency(currency));
                return currency.clone();
            }
        } catch (Exception e) {
            LOG.debug("Failed to GET currency with name '" + name + "' (" + e.getMessage() + ")");
        }
        return null;
    }

    /**
     * Returns a list of all the currency instances within the db
     *
     * @return instance of all the currency's in the db, if any exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String, Object)
     */
    public static List<Currency> get() {
        try {
            return SQLDirect.queryArray("SELECT * FROM " + CURRENCY_TABLE, new Currency());
        } catch (Exception e) {
            LOG.debug("Failed to GET autoranks (" + e.getMessage() + ")");
        }
        return new ArrayList<>();
    }

    /**
     * Creates a new currency in the db
     *
     * @param currency instance of the currency to be created
     * @return currency instance with all the data filled out, in this case 'id' is returned
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object, boolean)
     */
    @Nullable
    public static Currency create(Currency currency) {
        try {
            currency.currencyID = (long) insert(CURRENCY_TABLE, Arrays.copyOfRange(CURRENCYS_COLUMNS, 1, CURRENCYS_COLUMNS.length), currency, true);
            currencyCache.put(currency.currencyID, new CacheCurrency(currency.clone()));
            nameCache.put(currency.displayName.toUpperCase(), new CacheID(currency.currencyID));
            return currency;
        } catch (Exception e) {
            LOG.debug("Failed to create currency '" + currency.displayName + "' (" + e.getMessage() + ")");
            LOG.debug("Currency: " + GSON.toJson(currency));
        }
        return null;
    }

    /**
     * Updates a currency entry in the db
     *
     * @param currency        instance of the currency to pull the updates from
     * @param columnsToUpdate list of the variables that need to be updated
     * @return if the update was successful
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#update(String, String[], String, String, Object)
     */
    public static boolean update(Currency currency, String[] columnsToUpdate) {
        try {
            update(CURRENCY_TABLE, columnsToUpdate, "currencyID", "" + currency.currencyID, currency);
            if (currencyCache.containsKey(currency.currencyID)) {
                currencyCache.get(currency.currencyID).currency = updateInfoLocal(columnsToUpdate, currency, currencyCache.get(currency.currencyID).currency);
                currencyCache.get(currency.currencyID).lastSync = System.currentTimeMillis();
            } else
                currencyCache.put(currency.currencyID, new CacheCurrency(currency));
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to Update currency '" + currency.currencyID + "' with name '" + currency.displayName + "' (" + e.getMessage() + ")");
            LOG.debug("Currency: " + GSON.toJson(currency));
        }
        return false;
    }

    /**
     * Delete / Remove a currency from the db
     *
     * @param id id of the currency to be deleted
     * @return if the currency was deleted successfully
     */
    public static boolean delete(long id) {
        try {
            delete(CURRENCY_TABLE, "currencyID", "" + id);
            invalidate(id);
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to delete currency '" + id + "'");
        }
        return false;
    }

    /**
     * Invalidates / removes the given currency entry
     *
     * @param id id if the currency to remove from the sql cache
     */
    public static void invalidate(long id) {
        currencyCache.remove(id);
        LOG.debug("Currency '" + id + "' has been invalidated, will update on next request!");
    }

    /**
     * Invalidates / removes the given currency's name entry
     *
     * @param name name of the currency to remove from the sql cache
     */
    public static void invalidate(String name) {
        nameCache.remove(name.toUpperCase());
        LOG.debug("Currency '" + name + "' has been invalidated, will update on next request!");
    }

    /**
     * Run periodically to cleanup the cache and remove expired / invalid entries
     */
    public static void cleanupCache() {
        LOG.info("Currency Cache cleanup has begun!");
        // ID / Main Cache
        List<Long> toBeRemoved = new ArrayList<>();
        for (long id : currencyCache.keySet())
            if (needsUpdate(currencyCache.get(id)))
                toBeRemoved.add(id);
        // Name Cache
        List<String> toBeRemovedName = new ArrayList<>();
        for (String name : nameCache.keySet())
            if (needsUpdate(nameCache.get(name)))
                toBeRemovedName.add(name);
        // Remove from Cache
        int count = 0;
        // ID Cache
        for (long id : toBeRemoved) {
            count++;
            invalidate(id);
        }
        // Name Cache
        for (String name : toBeRemovedName) {
            count++;
            invalidate(name);
        }
        LOG.debug("Currency Cache has been cleaned, " + count + " entries have been removed!");
    }

    /**
     * Run periodically to cleanup the db and remove expired / invalid entries
     */
    public static void cleanupDB() {
    }
}
