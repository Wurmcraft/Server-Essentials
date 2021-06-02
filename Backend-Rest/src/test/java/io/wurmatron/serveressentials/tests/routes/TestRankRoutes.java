package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.tests.sql.TestRanks;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRankRoutes {

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.main(new String[]{});
        ServerEssentialsRest.config.server.host = "localhost";
        HTTPRequests.BASE_URL = "http://" + ServerEssentialsRest.config.server.host + ":" + ServerEssentialsRest.config.server.port + "/";
        // TODO Add Authentication or force config.testing when running tests
    }

    @Test
    @Order(1)
    public void testAddRank() throws IOException {
        Rank createdRank = HTTPRequests.postWithReturn("api/rank", TestRanks.TEST_RANK, Rank.class);
        assertNotNull(createdRank, "Rank is not null");
        TestRanks.TEST_RANK.rankID = createdRank.rankID;
        assertEquals(TestRanks.TEST_RANK, createdRank, "Ranks are the same");
        // Check if rank was added
        createdRank = HTTPRequests.get("api/rank/" + createdRank.name, Rank.class);
        TestRanks.TEST_RANK.rankID = createdRank.rankID;
        assertNotNull(createdRank, "Rank is not null");
        assertEquals(TestRanks.TEST_RANK, createdRank);
    }

    @Test
    @Order(2)
    public void testGetRank() throws IOException {
        Rank rank = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name, Rank.class);
        assertNotNull(rank, "Rank is not null");
        assertEquals(TestRanks.TEST_RANK, rank);
        TestRanks.TEST_RANK.rankID = rank.rankID;
        assertEquals(TestRanks.TEST_RANK, rank, "Ranks are the same");
    }

    @Test
    @Order(2)
    public void testGetName() throws IOException {
        String name = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/name", Rank.class).name;
        assertEquals(TestRanks.TEST_RANK.name, name, "Rank Name is the same");
    }

    @Test
    @Order(2)
    public void testGetPerms() throws IOException {
        String[] perms = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/permission", Rank.class).permissions;
        assertArrayEquals(TestRanks.TEST_RANK.permissions, perms, "Rank Perms are the same");
    }

    @Test
    @Order(2)
    public void testGetInheritance() throws IOException {
        String[] inheritance = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/inheritance", Rank.class).inheritance;
        assertArrayEquals(TestRanks.TEST_RANK.inheritance, inheritance, "Rank Inheritance is the same");
    }

    @Test
    @Order(2)
    public void testGetPrefix() throws IOException {
        String prefix = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/prefix", Rank.class).prefix;
        assertEquals(TestRanks.TEST_RANK.prefix, prefix, "Rank Prefix is the same");
    }

    @Test
    @Order(2)
    public void testGetPrefixPriority() throws IOException {
        int prefixPriority = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/prefix-priority", Rank.class).prefixPriority;
        assertEquals(TestRanks.TEST_RANK.prefixPriority, prefixPriority, "Rank Prefix Priority  is the same");
    }

    @Test
    @Order(2)
    public void testGetSuffix() throws IOException {
        String suffix = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/suffix", Rank.class).suffix;
        assertEquals(TestRanks.TEST_RANK.suffix, suffix, "Rank suffix is the same");
    }

    @Test
    @Order(2)
    public void testGetSuffixPriority() throws IOException {
        int suffixPriority = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/suffix-priority", Rank.class).suffixPriority;
        assertEquals(TestRanks.TEST_RANK.suffixPriority, suffixPriority, "Rank Suffix Priority  is the same");
    }

    @Test
    @Order(2)
    public void testGetColor() throws IOException {
        String color = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/color", Rank.class).color;
        assertEquals(TestRanks.TEST_RANK.color, color, "Rank Color is the same");
    }

    @Test
    @Order(2)
    public void testGetColorPriority() throws IOException {
        int colorPriority = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name + "/color-priority", Rank.class).colorPriority;
        assertEquals(TestRanks.TEST_RANK.colorPriority, colorPriority, "Rank Color Priority  is the same");
    }

    @Test
    @Order(2)
    public void testRankPatch() throws IOException {
        TestRanks.TEST_RANK.color = "red";
        int status = HTTPRequests.patch("api/rank/" + TestRanks.TEST_RANK.name + "/color", TestRanks.TEST_RANK);
        assertEquals(HttpURLConnection.HTTP_OK, status, "Rank has been updated");
        // Make sure its updated
        String color = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name, Rank.class).color;
        assertEquals(TestRanks.TEST_RANK.color, color, "Color has been updated");
    }

    // TODO Test for /rank #GetRank

    @Test
    @Order(3)
    public void testDeleteRank() throws IOException {
        Rank deletedRank = HTTPRequests.deleteWithReturn("api/rank/" + TestRanks.TEST_RANK.name, null, Rank.class);
        assertNotNull(deletedRank, "Deleted Rank should be returned");
        // Make sure its deleted
        deletedRank = HTTPRequests.get("api/rank/" + TestRanks.TEST_RANK.name, Rank.class);
        assertNull(deletedRank, "Rank has been deleted");
    }
}
