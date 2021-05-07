package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.Currency;
import io.wurmatron.serveressentials.sql.SQLCache;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SQLCacheCurrency extends SQLCache {

    public static String CURRENCY_TABLE = "currencys";

    /**
     * Returns the given instance based on the id, if one exists
     * @param id id of the currency
     * @return instance of the currency, from the db, if exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object) 
     */
    // TODO Implement
    @Nullable
    public static Currency get(long id) {
        return null;
    }

    /**
     * Returns the given instance based on the name, if one exists
     * @param name name of the currency
     * @return instance of the currency, from the db, if exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object)
     */
    // TODO Implement
    @Nullable
    public static Currency get(String name) {
        return null;
    }

    /**
     * Returns a list of all the currency instances within the db
     *
     * @return instance of all the currency's in the db, if any exists
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String, Object)
     */
    // TODO Implement
    public static List<Currency> get() {
        return new ArrayList<>();
    }

    /**
     * Creates a new currency in the db
     *
     * @param currency instance of the currency to be created
     * @return currency instance with all the data filled out, in this case 'id' is returned
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object, boolean)
     */
    // TODO Implement
    @Nullable
    public static Currency create(Currency currency) {
        return null;
    }

    /**
     * Updates a currency entry in the db
     *
     * @param currency             instance of the currency to pull the updates from
     * @param columnsToUpdate list of the variables that need to be updated
     * @return if the update was successful
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#update(String, String[], String, String, Object)
     */
    // TODO Implement
    public static boolean update(Currency currency, String[] columnsToUpdate) {
        return false;
    }

    /**
     * Delete / Remove a currency from the db
     *
     * @param id id of the currency to be deleted
     * @return if the currency was deleted successfully
     */
    // TODO Implement
    public static boolean delete(long id) {
        return false;
    }

    /**
     * Invalidates / removes the given currency entry
     *
     * @param id id if the currency to remove from the sql cache
     */
    // TODO Implement
    public static void invalidate(long id) {

    }

    /**
     * Invalidates / removes the given currency's name entry
     *
     * @param name name of the currency to remove from the sql cache
     */
    // TODO Implement
    public static void invalidate(String name) {

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
