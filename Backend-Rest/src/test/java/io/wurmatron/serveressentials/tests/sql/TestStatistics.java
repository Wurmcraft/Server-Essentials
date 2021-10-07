package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.TrackedStat;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLStatistics;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestStatistics {

    public static final TrackedStat TEST_STAT = new TrackedStat("Test", TestAccounts.TEST_ACCOUNT.uuid, Instant.now().getEpochSecond(), "break", "{}");

    @BeforeAll
    public static void setup() throws Exception {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddStat() {
        TrackedStat stat = SQLStatistics.create(TEST_STAT);
        assertNotNull(stat, "Stat entry has been successfully created without errors");
        // Check if it was added
        List<TrackedStat> userStats = SQLStatistics.get(TEST_STAT.uuid);
        boolean found = false;
        for (TrackedStat ts : userStats)
            if (ts.equals(TEST_STAT))
                found = true;
        assertTrue(found, "Added Stat Entry exists");
    }

    @Test
    @Order(2)
    public void testUpdateStat() {
        TEST_STAT.event_data = "{\"x\": 5}";
        boolean updated = SQLStatistics.update(TEST_STAT, new String[]{"eventData"});
        assertTrue(updated, "Stat has been successfully updated without issues");
        // Check if it was updated
        List<TrackedStat> userStats = SQLStatistics.get(TEST_STAT.uuid);
        boolean found = false;
        for (TrackedStat ts : userStats)
            if (ts.equals(TEST_STAT))
                found = true;
        assertTrue(found, "Updated Stat Entry exists");
    }

    @Test
    @Order(2)
    public void testGetStat() {
        List<TrackedStat> stats = SQLStatistics.get(TEST_STAT.server_id, TEST_STAT.uuid);
        // Check if stat entry exists
        boolean found = false;
        for (TrackedStat ts : stats)
            if (ts.equals(TEST_STAT))
                found = true;
        assertTrue(found, "Stat Entry exists");
    }

    @Test
    @Order(3)
    public void testDeleteStatEntry() {
        boolean deleted = SQLStatistics.delete(TEST_STAT.server_id, TEST_STAT.uuid, TEST_STAT.event_type);
        assertTrue(deleted, "Tracked Stat has been successfully deleted without errors");
        // Make sure its deleted
        List<TrackedStat> stats = SQLStatistics.get(TEST_STAT.server_id, TEST_STAT.uuid);
        // Check if stat entry exists
        boolean found = false;
        for (TrackedStat ts : stats)
            if (ts.equals(TEST_STAT))
                found = true;
        assertFalse(found, "Stat Entry does not exist");
    }
}
