package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Currency;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.cache_holder.CacheCurrency;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class SQLCacheCurrency extends SQLCache {

    public static String CURRENCY_TABLE = "currencys";

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
        if (currencyCache.containsKey(name.toUpperCase()))
            if (!needsUpdate(currencyCache.get(name.toUpperCase())))
                return currencyCache.get(name.toUpperCase()).currency;
            else
                invalidate(name.toUpperCase());
        // Invalid / missing
        try {
            Currency currency = get("*", CURRENCY_TABLE, "displayName", name, new Currency());
            if (currency != null) {
                currencyCache.put(currency.display_name.toUpperCase(), new CacheCurrency(currency));
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
            insert(CURRENCY_TABLE, Arrays.copyOfRange(CURRENCYS_COLUMNS, 1, CURRENCYS_COLUMNS.length), currency, false);
            currencyCache.put(currency.display_name.toUpperCase(), new CacheCurrency(currency.clone()));
            return currency;
        } catch (Exception e) {
            LOG.debug("Failed to create currency '" + currency.display_name + "' (" + e.getMessage() + ")");
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
            update(CURRENCY_TABLE, columnsToUpdate, "displayName", "" + currency.display_name, currency);
            if (currencyCache.containsKey(currency.display_name.toUpperCase())) {
                currencyCache.get(currency.display_name).currency = updateInfoLocal(columnsToUpdate, currency, currencyCache.get(currency.display_name).currency);
                currencyCache.get(currency.display_name).lastSync = System.currentTimeMillis();
            } else
                currencyCache.put(currency.display_name.toUpperCase(), new CacheCurrency(currency));
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to Update currency with name '" + currency.display_name + "' (" + e.getMessage() + ")");
            LOG.debug("Currency: " + GSON.toJson(currency));
        }
        return false;
    }

    /**
     * Delete / Remove a currency from the db
     *
     * @param name Name of the currency to be deleted
     * @return if the currency was deleted successfully
     */
    public static boolean delete(String name) {
        try {
            delete(CURRENCY_TABLE, "display_name", "" + name);
            invalidate(name);
            return true;
        } catch (Exception e) {
            LOG.debug("Failed to delete currency '" + name + "'");
        }
        return false;
    }

    /**
     * Invalidates / removes the given currency's name entry
     *
     * @param name name of the currency to remove from the sql cache
     */
    public static void invalidate(String name) {
        currencyCache.remove(name.toUpperCase());
        LOG.debug("Currency '" + name + "' has been invalidated, will update on next request!");
    }

    /**
     * Run periodically to cleanup the cache and remove expired / invalid entries
     */
    public static void cleanupCache() {
        LOG.info("Currency Cache cleanup has begun!");
        List<String> toBeRemoved = new ArrayList<>();
        for (String name : currencyCache.keySet())
            if (needsUpdate(currencyCache.get(name)))
                toBeRemoved.add(name);
        // Remove from Cache
        int count = 0;
        //  Cache
        for (String name : toBeRemoved) {
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

    /**
     * Get all the columns except the id
     */
    public static String[] getColumns() {
        return Arrays.copyOfRange(CURRENCYS_COLUMNS, 1, CURRENCYS_COLUMNS.length);
    }
}
