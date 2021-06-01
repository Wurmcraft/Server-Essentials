package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.LogEntry;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLLogging;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestLogging {

    public static final LogEntry TEST_ENTRY = new LogEntry("Test", Instant.now().getEpochSecond(), "Break", "{}", TestAccounts.TEST_ACCOUNT.uuid, 5, 5, 255, 10);

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddEntry() {
        LogEntry entry = SQLLogging.create(TEST_ENTRY);
        assertNotNull(entry, "Log Entry has been successfully created without errors");
        // Check if the  entry was added
        List<LogEntry> logEntries = SQLLogging.get(TEST_ENTRY.serverID, TEST_ENTRY.x, TEST_ENTRY.y, TEST_ENTRY.z, TEST_ENTRY.dim);
        boolean found = false;
        for (LogEntry log : logEntries)
            if (TEST_ENTRY.equals(log)) {
                found = true;
                break;
            }
        assertTrue(found, "Added log entry");
    }

    @Test
    @Order(2)
    public void testGetEntry() {
        List<LogEntry> logEntries = SQLLogging.get(TEST_ENTRY.serverID, TEST_ENTRY.x, TEST_ENTRY.y, TEST_ENTRY.z, TEST_ENTRY.dim);
        boolean found = false;
        for (LogEntry log : logEntries)
            if (TEST_ENTRY.equals(log)) {
                found = true;
                break;
            }
        assertTrue(found, "Entry exists");
    }

    @Test
    @Order(2)
    public void testUpdateEntry() {
        TEST_ENTRY.actionData = "{\"fire\": true}";
        boolean updated = SQLLogging.update(TEST_ENTRY, new String[]{"actionData"});
        assertTrue(updated, "Log Entry was updated successfully without errors");
        // Check if the entry was updated
        List<LogEntry> logEntries = SQLLogging.get(TEST_ENTRY.serverID, TEST_ENTRY.x, TEST_ENTRY.y, TEST_ENTRY.z, TEST_ENTRY.dim);
        boolean found = false;
        for (LogEntry log : logEntries)
            if (TEST_ENTRY.equals(log)) {
                found = true;
                break;
            }
        assertTrue(found, "Entry exists");
    }

    @Test
    @Order(3)
    public void testDeleteEntry() {
        boolean deleted = SQLLogging.delete(TEST_ENTRY.serverID, TEST_ENTRY.actionType, TEST_ENTRY.uuid, TEST_ENTRY.timestamp);
        assertTrue(deleted, "Log Entry was deleted successfully without any errors");
        // Check to see if it was deleted
        List<LogEntry> logEntries = SQLLogging.get(TEST_ENTRY.serverID, TEST_ENTRY.x, TEST_ENTRY.y, TEST_ENTRY.z, TEST_ENTRY.dim);
        boolean found = false;
        for (LogEntry log : logEntries)
            if (TEST_ENTRY.equals(log)) {
                found = true;
                break;
            }
        assertFalse(found, "Entry does not exist");
    }
}
