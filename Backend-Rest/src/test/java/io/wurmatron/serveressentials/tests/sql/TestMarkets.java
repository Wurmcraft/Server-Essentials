package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.MarketEntry;
import io.wurmatron.serveressentials.models.transfer.ItemWrapper;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheMarket;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMarkets {

    public static final MarketEntry TEST_MARKET = new MarketEntry("test", TestAccounts.TEST_ACCOUNT.uuid, new ItemWrapper("<diamond>"), "test", 500, Instant.now().getEpochSecond(), "Sell", "{}", "");

    @BeforeAll
    public static void setup() throws Exception {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddMarketEntry() {
        MarketEntry market = SQLCacheMarket.create(TEST_MARKET);
        assertNotNull(market, "Market Entry has been successfully created without errors");
        // Check if entry was added
        List<MarketEntry> entrys = SQLCacheMarket.get(TEST_MARKET.server_id, TEST_MARKET.seller_uuid);
        boolean found = false;
        for (MarketEntry entry : entrys)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertTrue(found, "Added market-entry exists");
    }

    @Test
    @Order(2)
    public void testGetMarketEntry() {
        List<MarketEntry> entrys = SQLCacheMarket.get(TEST_MARKET.server_id, TEST_MARKET.seller_uuid);
        boolean found = false;
        for (MarketEntry entry : entrys)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertTrue(found, "Added market-entry exists");
        // Remove from cache and try again
        SQLCacheMarket.invalidate(TEST_MARKET.seller_uuid);
        entrys = SQLCacheMarket.get(TEST_MARKET.server_id, TEST_MARKET.seller_uuid);
        found = false;
        for (MarketEntry entry : entrys)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertTrue(found, "Added market-entry exists");
    }

    @Test
    @Order(2)
    public void testUpdateMarketEntry() {
        TEST_MARKET.transfer_id = "Test";
        boolean updated = SQLCacheMarket.update(TEST_MARKET, new String[]{"transfer_id"});
        assertTrue(updated, "Market Entry has been successfully updated without errors");
        // Check for updates
        List<MarketEntry> entries = SQLCacheMarket.get(TEST_MARKET.server_id, TEST_MARKET.seller_uuid);
        boolean found = false;
        for (MarketEntry entry : entries)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertTrue(found, "Added market-entry exists");
        // Invalidate Cache and try again
        SQLCacheMarket.invalidate(TEST_MARKET.seller_uuid);
        entries = SQLCacheMarket.get(TEST_MARKET.server_id, TEST_MARKET.seller_uuid);
        found = false;
        for (MarketEntry entry : entries)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertTrue(found, "Added market-entry exists");
    }

    @Test
    @Order(3)
    public void testDeleteMarketEntry() {
        boolean deleted = SQLCacheMarket.delete(TEST_MARKET.server_id, TEST_MARKET.seller_uuid, TEST_MARKET.timestamp);
        assertTrue(deleted, "Market Entry has been successfully deleted without errors");
        // Make sure it was deleted
        List<MarketEntry> entries = SQLCacheMarket.get(TEST_MARKET.server_id, TEST_MARKET.seller_uuid);
        boolean found = false;
        for (MarketEntry entry : entries)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertFalse(found, "Market Entry does not exist");
        // Invalidate Cache and try again
        SQLCacheMarket.invalidate(TEST_MARKET.server_id);
        entries = SQLCacheMarket.get(TEST_MARKET.server_id, TEST_MARKET.seller_uuid);
        found = false;
        for (MarketEntry entry : entries)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertFalse(found, "Market Entry does not exist");
    }
}
