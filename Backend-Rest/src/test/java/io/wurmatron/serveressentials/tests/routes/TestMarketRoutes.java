package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.MarketEntry;
import io.wurmatron.serveressentials.tests.sql.TestMarkets;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestMarketRoutes {

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        if(!isSetup) {
            ServerEssentialsRest.main(new String[]{});
            HTTPRequests.BASE_URL = "http://" + ServerEssentialsRest.config.server.host + ":" + ServerEssentialsRest.config.server.port + "/";
            // TODO Add Authentication or force config.testing when running tests
            isSetup = true;
        }
    }

    @Test
    @Order(1)
    public void testAddMarketEntry() throws IOException {
        MarketEntry newEntry = HTTPRequests.postWithReturn("market", TestMarkets.TEST_MARKET, MarketEntry.class);
        assertNotNull(newEntry, "Market Entry is not null");
        // Check if the new entry was created
        MarketEntry[] entries = HTTPRequests.get("api/market?uuid=" + TestMarkets.TEST_MARKET.sellerUUID, MarketEntry[].class);
        boolean exists = false;
        for (MarketEntry entry : entries)
            if (TestMarkets.TEST_MARKET.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Market entry was added");
    }

    @Test
    @Order(2)
    public void testGetMarketEntry() throws IOException {
        MarketEntry[] entries = HTTPRequests.get("api/market?uuid=" + TestMarkets.TEST_MARKET.sellerUUID, MarketEntry[].class);
        boolean exists = false;
        for (MarketEntry entry : entries)
            if (TestMarkets.TEST_MARKET.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Market entry exists");
    }

    @Test
    @Order(2)
    public void testUpdateMarketEntry() throws IOException {
        TestMarkets.TEST_MARKET.marketType = "BUY";
        HTTPRequests.put("market", TestMarkets.TEST_MARKET);
        // Make sure it was updated
        MarketEntry[] entries = HTTPRequests.get("api/market?uuid=" + TestMarkets.TEST_MARKET.sellerUUID, MarketEntry[].class);
        boolean exists = false;
        for (MarketEntry entry : entries)
            if (TestMarkets.TEST_MARKET.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Market entry was updated successfully");
    }

    @Test
    @Order(3)
    public void testDeleteMarketEntry() throws IOException {
        MarketEntry deletedEntry = HTTPRequests.deleteWithReturn("api/market", TestMarkets.TEST_MARKET, MarketEntry.class);
        assertNotNull(deletedEntry, "Deleted entry is not null");
        // Make sure the entry was deleted
        MarketEntry[] entries = HTTPRequests.get("api/market?uuid=" + TestMarkets.TEST_MARKET.sellerUUID, MarketEntry[].class);
        boolean exists = false;
        for (MarketEntry entry : entries)
            if (TestMarkets.TEST_MARKET.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Market entry was updated successfully");
    }
}
