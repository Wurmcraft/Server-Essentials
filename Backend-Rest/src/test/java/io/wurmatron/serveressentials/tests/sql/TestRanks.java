package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestRanks {

    public static final Rank TEST_RANK = new Rank(-1, "Test", new String[]{"general.*", "language.*"}, new String[]{"Test"}, "[Test]", 0, "", 0, "", 0);

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddRank() {
        Rank addedRank = SQLCacheRank.create(TEST_RANK);
        assertNotNull(addedRank, "Rank has been successfully updated without errors");
        TEST_RANK.rankID = addedRank.rankID;
        addedRank = SQLCacheRank.getID(TEST_RANK.rankID);
        assertNotNull(addedRank, "Rank exists");
        assertEquals(TEST_RANK, addedRank, "Rank should be the same");
    }

    @Test
    @Order(2)
    public void testGetRank() {
        Rank rank = SQLCacheRank.getID(TEST_RANK.rankID);
        assertNotNull(rank, "Rank exists");
        // Remove from cache and try again
        SQLCacheRank.invalidate(TEST_RANK.rankID);
        rank = SQLCacheRank.getID(TEST_RANK.rankID);
        assertNotNull(rank, "Rank exists");
        assertEquals(TEST_RANK, rank, "Rank is the same");
    }

    @Test
    @Order(2)
    public void testGetRanks() {
        List<Rank> ranks = SQLCacheRank.gets();
        assertTrue(ranks.size() > 0, "Ranks exist");
        // Remove from cache and try again
        for (Rank rank : ranks)
            SQLCacheRank.invalidate(rank.rankID);
        ranks = SQLCacheRank.gets();
        assertTrue(ranks.size() > 0, "Ranks exist");
        boolean found = false;
        for (Rank rank : ranks)
            if (rank.equals(TEST_RANK)) {
                found = true;
                break;
            }
        assertTrue(found, "Added rank exists");
    }

    @Test
    @Order(2)
    public void testUpdateRank() {
        TEST_RANK.name = "Test2";
        boolean updated = SQLCacheRank.update(TEST_RANK);
        assertTrue(updated, "Rank has been updated updated without errors");
        Rank rank = SQLCacheRank.getID(TEST_RANK.rankID);
        assertEquals(TEST_RANK, rank, "Rank is the same");
        // Remove from cache and try again
        rank = SQLCacheRank.getID(TEST_RANK.rankID);
        assertEquals(TEST_RANK, rank, "Rank is the same");
    }

    @Test
    @Order(3)
    public void testDeleteRank() {
        boolean deleted = SQLCacheRank.delete(TEST_RANK.rankID);
        assertTrue(deleted, "Rank has been successfully deleted without errors");
        // Make sure it was deleted
        Rank rank = SQLCacheRank.getID(TEST_RANK.rankID);
        assertNull(rank, "Rank does not exist");
        // Invalidate Cache and try again
        SQLCacheRank.invalidate(TEST_RANK.rankID);
        rank = SQLCacheRank.getID(TEST_RANK.rankID);
        assertNull(rank, "Rank does not exist");
    }

}
