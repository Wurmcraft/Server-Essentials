package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.tests.sql.TestAutoRanks;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAutoRankRoutes {

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
    public void testAddAutoRank() throws IOException {
        AutoRank addedRank = HTTPRequests.postWithReturn("api/autorank", TestAutoRanks.TEST_AUTORANK, AutoRank.class);
        assertNotNull(addedRank, "Auto-Rank was added successfully");
        // Make sure the auto-rank was added
        AutoRank autorank = HTTPRequests.get("api/autorank/" + addedRank.rank, AutoRank.class);
        assertEquals(TestAutoRanks.TEST_AUTORANK, autorank, "Auto-Ranks are the same");
    }

    @Test
    @Order(2)
    public void testGetAutoRank() throws IOException {
        AutoRank autorank = HTTPRequests.get("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank, AutoRank.class);
        assertEquals(TestAutoRanks.TEST_AUTORANK, autorank, "Auto-Ranks are the same");
    }

    @Test
    @Order(2)
    public void testUpdateAutoRank() throws IOException {
        TestAutoRanks.TEST_AUTORANK.playTime = 4000L;
        HTTPRequests.put("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank, TestAutoRanks.TEST_AUTORANK);
        // Check if the auto-rank has ranked up
        AutoRank[] autoranks = HTTPRequests.get("api/autorank", AutoRank[].class);
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
        String rank = HTTPRequests.get("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank + "/rank", AutoRank.class).rank;
        assertEquals(TestAutoRanks.TEST_AUTORANK.rank, rank, "Rank is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificNextAutoRank() throws IOException {
        String nextRank = HTTPRequests.get("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank + "/next-rank", AutoRank.class).nextRank;
        assertEquals(TestAutoRanks.TEST_AUTORANK.nextRank, nextRank, "Next-Rank is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificPlaytimeAutoRank() throws IOException {
        long playtime = HTTPRequests.get("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank + "/playtime", AutoRank.class).playTime;
        assertEquals(TestAutoRanks.TEST_AUTORANK.playTime, playtime, "Playtime is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificCurrencyAutoRank() throws IOException {
        String currencyName = HTTPRequests.get("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank + "/currency", AutoRank.class).currencyName;
        assertEquals(TestAutoRanks.TEST_AUTORANK.currencyName, currencyName, "Currency-Name is the same");
    }

    @Test
    @Order(2)
    public void testGetSpecificCurrencyAmountAutoRank() throws IOException {
        double amount = HTTPRequests.get("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank + "/currency-amount", AutoRank.class).currencyAmount;
        assertEquals(TestAutoRanks.TEST_AUTORANK.currencyAmount, amount, "Currency Amount is the same");
    }

    @Test
    @Order(3)
    public void testDeleteAutoRank() throws IOException {
        AutoRank deletedRank = HTTPRequests.deleteWithReturn("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank, TestAutoRanks.TEST_AUTORANK, AutoRank.class);
        assertNotNull(deletedRank, "AutoRank was deleted");
        // Make sure the autorank was deleted
        AutoRank autorank = HTTPRequests.get("api/autorank/" + TestAutoRanks.TEST_AUTORANK.rank, AutoRank.class);
        assertNull( autorank, "Auto-Ranks has been deleted");
    }
}
