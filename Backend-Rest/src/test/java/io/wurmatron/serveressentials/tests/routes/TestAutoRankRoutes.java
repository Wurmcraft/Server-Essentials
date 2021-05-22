package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.tests.sql.TestAutoRanks;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAutoRankRoutes {

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.main(new String[]{});
        HTTPRequests.BASE_URL = "http://" + ServerEssentialsRest.config.server.host + ":" + ServerEssentialsRest.config.server.port + "/";
        // TODO Add Authentication or force config.testing when running tests
    }

    @Test
    @Order(1)
    public void testAddAutoRank() throws IOException {
        AutoRank addedRank = HTTPRequests.postWithReturn("autorank", TestAutoRanks.TEST_AUTORANK, AutoRank.class);
        assertNotNull(addedRank, "Auto-Rank was added successfully");
        // Make sure the auto-rank was added
        TestAutoRanks.TEST_AUTORANK.autoRankID = addedRank.autoRankID;
        AutoRank autorank = HTTPRequests.get("autorank/" + addedRank.autoRankID, AutoRank.class);
        assertEquals(TestAutoRanks.TEST_AUTORANK, autorank, "Auto-Ranks are the same");
    }

    @Test
    @Order(2)
    public void testGetAutoRank() throws IOException {
        AutoRank autorank = HTTPRequests.get("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID, AutoRank.class);
        assertEquals(TestAutoRanks.TEST_AUTORANK, autorank, "Auto-Ranks are the same");
    }

    @Test
    @Order(2)
    public void testUpdateAutoRank() throws IOException {
        TestAutoRanks.TEST_AUTORANK.playtime = 4000;
        HTTPRequests.put("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID, TestAutoRanks.TEST_AUTORANK);
        // Check if the auto-rank has ranked up
        AutoRank[] autoranks = HTTPRequests.get("autorank", AutoRank[].class);
        boolean exists = false;
        for (AutoRank ar : autoranks)
            if (TestAutoRanks.TEST_AUTORANK.equals(ar)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Auto-Rank has been updated");
    }

    @Test
    @Order(2)
    public void testGetSpecificAutoRank() throws IOException {
        String rank = HTTPRequests.get("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID + "/rank", AutoRank.class).rank;
        assertEquals(TestAutoRanks.TEST_AUTORANK.rank, rank, "Rank is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificNextAutoRank() throws IOException {
        String nextRank = HTTPRequests.get("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID + "/next-rank", AutoRank.class).nextRank;
        assertEquals(TestAutoRanks.TEST_AUTORANK.nextRank, nextRank, "Next-Rank is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificPlaytimeAutoRank() throws IOException {
        long playtime = HTTPRequests.get("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID + "/playtime", AutoRank.class).playtime;
        assertEquals(TestAutoRanks.TEST_AUTORANK.playtime, playtime, "Playtime is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificCurrencyAutoRank() throws IOException {
        String currencyName = HTTPRequests.get("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID + "/currency", AutoRank.class).currencyName;
        assertEquals(TestAutoRanks.TEST_AUTORANK.currencyName, currencyName, "Currency-Name is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificCurrencyAmountAutoRank() throws IOException {
        double amount = HTTPRequests.get("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID + "/currency-amount", AutoRank.class).currencyAmount;
        assertEquals(TestAutoRanks.TEST_AUTORANK.currencyAmount, amount, "Currency Amount is the same");
    }

    @Test
    @Order(3)
    public void testDeleteAutoRank() throws IOException {
        AutoRank deletedRank = HTTPRequests.deleteWithReturn("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID, TestAutoRanks.TEST_AUTORANK, AutoRank.class);
        assertNotNull(deletedRank, "AutoRank was deleted");
        // Make sure the autorank was deleted
        AutoRank autorank = HTTPRequests.get("autorank/" + TestAutoRanks.TEST_AUTORANK.autoRankID, AutoRank.class);
        assertEquals(TestAutoRanks.TEST_AUTORANK, autorank, "Auto-Ranks are the same");
    }
}