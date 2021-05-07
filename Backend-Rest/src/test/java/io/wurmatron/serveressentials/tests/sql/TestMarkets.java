package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.MarketEntry;
import io.wurmatron.serveressentials.models.transfer.ItemWrapper;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheMarket;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMarkets {

    public static final MarketEntry TEST_MARKET = new MarketEntry("test", TestAccounts.TEST_ACCOUNT.uuid, new ItemWrapper(""), "test", 500, Instant.now().getEpochSecond(), "Sell", "{}", "");

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddMarketEntry() {
        MarketEntry market = SQLCacheMarket.create(TEST_MARKET);
        assertNotNull(market, "Market Entry has been successfully created without errors");
        // Check if entry was added
        List<MarketEntry> entrys = SQLCacheMarket.get(TEST_MARKET.serverID, TEST_MARKET.sellerUUID);
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
        List<MarketEntry> entrys = SQLCacheMarket.get(TEST_MARKET.serverID, TEST_MARKET.sellerUUID);
        boolean found = false;
        for (MarketEntry entry : entrys)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertTrue(found, "Added market-entry exists");
        // Remove from cache and try again
        SQLCacheMarket.invalidate(TEST_MARKET.sellerUUID);
        entrys = SQLCacheMarket.get(TEST_MARKET.serverID, TEST_MARKET.sellerUUID);
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
        TEST_MARKET.transferID = "Test";
        boolean updated = SQLCacheMarket.update(TEST_MARKET, new String[]{"transferID"});
        assertTrue(updated, "Market Entry has been successfully updated without errors");
        // Check for updates
        List<MarketEntry> entries = SQLCacheMarket.get(TEST_MARKET.serverID, TEST_MARKET.sellerUUID);
        boolean found = false;
        for (MarketEntry entry : entries)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertTrue(found, "Added market-entry exists");
        // Invalidate Cache and try again
        SQLCacheMarket.invalidate(TEST_MARKET.sellerUUID);
        entries = SQLCacheMarket.get(TEST_MARKET.serverID, TEST_MARKET.sellerUUID);
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
        boolean deleted = SQLCacheMarket.delete(TEST_MARKET.serverID, TEST_MARKET.sellerUUID, TEST_MARKET.timestamp);
        assertTrue(deleted, "Market Entry has been successfully deleted without errors");
        // Make sure it was deleted
        List<MarketEntry> entries = SQLCacheMarket.get(TEST_MARKET.serverID, TEST_MARKET.sellerUUID);
        boolean found = false;
        for (MarketEntry entry : entries)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertFalse(found, "Market Entry does not exist");
        // Invalidate Cache and try again
        SQLCacheMarket.invalidate(TEST_MARKET.serverID);
        entries = SQLCacheMarket.get(TEST_MARKET.serverID, TEST_MARKET.sellerUUID);
        found = false;
        for (MarketEntry entry : entries)
            if (entry.equals(TEST_MARKET)) {
                found = true;
                break;
            }
        assertFalse(found, "Market Entry does not exist");
    }
}
