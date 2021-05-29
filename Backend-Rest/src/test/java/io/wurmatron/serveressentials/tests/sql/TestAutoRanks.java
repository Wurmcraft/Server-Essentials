package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAutoRank;
import io.wurmatron.serveressentials.sql.routes.SQLCacheRank;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAutoRanks {

    public static final AutoRank TEST_AUTORANK = new AutoRank(-1,"Test", "Test2", 500,"TesT", 500, "{}");

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddAutoRank() {
        AutoRank addedAutoRank = SQLCacheAutoRank.create(TEST_AUTORANK);
        assertNotNull(addedAutoRank,"AutoRank has been successfully created without errors");
        TEST_AUTORANK.autoRankID = addedAutoRank.autoRankID;
        // Check for Auto Rank
        addedAutoRank = SQLCacheAutoRank.getID(TEST_AUTORANK.autoRankID);
        assertNotNull(addedAutoRank, "Auto-Rank Exists");
        assertEquals(TEST_AUTORANK,addedAutoRank, "Auto-Ranks should be the same");
    }

    @Test
    @Order(2)
    public void testGetAutoRank() {
        AutoRank autorank = SQLCacheAutoRank.getID(TEST_AUTORANK.autoRankID);
        assertNotNull(autorank, "Auto-Rank exists");
        // Remove from cache and try again
        SQLCacheAutoRank.invalidate(TEST_AUTORANK.autoRankID);
        autorank = SQLCacheAutoRank.getID(TEST_AUTORANK.autoRankID);
        assertNotNull(autorank, "Auto-Rank exists");
        assertEquals(TEST_AUTORANK, autorank, "Auto-Rank is the same");
    }

    @Test
    @Order(2)
    public void testGetAutoRanks() {
        List<AutoRank> autoranks = SQLCacheAutoRank.get();
        assertTrue(autoranks.size() > 0, "Auto-Ranks exist");
        // Remove from cache and try again
        for (AutoRank ar : autoranks)
            SQLCacheRank.invalidate(ar.autoRankID);
        autoranks = SQLCacheAutoRank.get();
        assertTrue(autoranks.size() > 0, "Auto-Ranks exist");
        boolean found = false;
        for (AutoRank ar : autoranks)
            if (ar.equals(TEST_AUTORANK)) {
                found = true;
                break;
            }
        assertTrue(found, "Added auto-rank exists");
    }

    @Test
    @Order(2)
    public void testUpdateAutoRank() {
        TEST_AUTORANK.playTime = 800L;
        boolean updated = SQLCacheAutoRank.update(TEST_AUTORANK, new String[] {"playTime"});
        assertTrue(updated, "Auto-Rank has been updated updated without errors");
        AutoRank autoRank = SQLCacheAutoRank.getID(TEST_AUTORANK.autoRankID);
        assertEquals(TEST_AUTORANK, autoRank, "AutoRank is the same");
        // Remove from cache and try again
        autoRank = SQLCacheAutoRank.getID(TEST_AUTORANK.autoRankID);
        assertEquals(TEST_AUTORANK, autoRank, "Auto-Rank is the same");
    }

    @Test
    @Order(3)
    public void testDeleteAutoRank() {
        boolean deleted = SQLCacheAutoRank.delete(TEST_AUTORANK.autoRankID);
        assertTrue(deleted, "Auto-Rank has been successfully deleted without errors");
        // Make sure it was deleted
        AutoRank autoRank = SQLCacheAutoRank.getID(TEST_AUTORANK.autoRankID);
        assertNull(autoRank, "Auto-Rank does not exist");
        // Invalidate Cache and try again
        SQLCacheAutoRank.invalidate(TEST_AUTORANK.autoRankID);
        autoRank = SQLCacheAutoRank.getID(TEST_AUTORANK.autoRankID);
        assertNull(autoRank, "Auto-Rank does not exist");
    }
}
