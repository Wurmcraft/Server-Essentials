package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Currency;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheCurrency;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCurrencys {

    public static final Currency TEST_CURRENCY = new Currency("Test", 1, 1, .1);

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddCurrency() {
        Currency currency = SQLCacheCurrency.create(TEST_CURRENCY);
        assertNotNull(currency, "Currency has been successfully created without errors");
        // Check for new currency
        currency = SQLCacheCurrency.get(currency.displayName);
        assertEquals(TEST_CURRENCY, currency, "Added currency should be the same as the one added");
    }

    @Test
    @Order(2)
    public void testGetCurrency() {
        Currency currency = SQLCacheCurrency.get(TEST_CURRENCY.displayName);
        assertEquals(TEST_CURRENCY, currency, "The Currency's are equal");
        // Remove from cache and try again
        SQLCacheCurrency.invalidate(TEST_CURRENCY.displayName);
        currency = SQLCacheCurrency.get(TEST_CURRENCY.displayName);
        // Test Currency
        assertNotNull(currency, "Cache has the currency");
        assertEquals(TEST_CURRENCY, currency, "Currency's are the same");
    }

    @Test
    @Order(2)
    public void testUpdateCurrency() {
        TEST_CURRENCY.displayName = "Test Updated";
        boolean updated = SQLCacheCurrency.update(TEST_CURRENCY,new String[] {"displayName"});
        assertTrue(updated, "Currency has been successfully updated without errors");
        // Check for updates
        Currency currency = SQLCacheCurrency.get(TEST_CURRENCY.displayName);
        assertNotNull(currency, "Currency Exists");
        assertEquals(TEST_CURRENCY.displayName, currency.displayName, "Display-Name has been updated");
        // Invalidate Cache and try again
        SQLCacheCurrency.invalidate(TEST_CURRENCY.displayName);
        currency = SQLCacheCurrency.get(currency.displayName);
        assertNotNull(currency, "Currency Exists");
        assertEquals(TEST_CURRENCY.displayName, currency.displayName, "Display-Name has been updated");
    }

    @Test
    @Order(2)
    public void testGetCurrencys() {
        List<Currency> currencies = SQLCacheCurrency.get();
        assertTrue(currencies.size() > 0, "Currency exist");
        // Remove from cache and try again
        for (Currency currency : currencies)
            SQLCacheRank.invalidate(currency.displayName);
        currencies = SQLCacheCurrency.get();
        assertTrue(currencies.size() > 0, "Currency exist");
        boolean found = false;
        for (Currency currency : currencies)
            if (currency.equals(TEST_CURRENCY)) {
                found = true;
                break;
            }
        assertTrue(found, "Added currency exists");
    }

    @Test
    @Order(3)
    public void testDeleteCurrency() {
        boolean deleted = SQLCacheCurrency.delete(TEST_CURRENCY.displayName);
        assertTrue(deleted, "Currency has been successfully deleted without errors");
        // Make sure it was deleted
        Currency currency = SQLCacheCurrency.get(TEST_CURRENCY.displayName);
        assertNull(currency, "Currency does not exist");
        // Invalidate Cache and try again
        SQLCacheCurrency.invalidate(TEST_CURRENCY.displayName);
        currency = SQLCacheCurrency.get(TEST_CURRENCY.displayName);
        assertNull(currency, "Currency does not exist");
    }
}
