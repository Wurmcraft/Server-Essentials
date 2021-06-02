package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.LogEntry;
import io.wurmatron.serveressentials.tests.sql.TestLogging;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class TestLoggingRoutes {

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.main(new String[]{});
        ServerEssentialsRest.config.server.host = "localhost";
        HTTPRequests.BASE_URL = "http://" + ServerEssentialsRest.config.server.host + ":" + ServerEssentialsRest.config.server.port + "/";
        // TODO Add Authentication or force config.testing when running tests
    }

    @Test
    @Order(1)
    public void testAddLoggingEvent() throws IOException {
        LogEntry newEntry = HTTPRequests.postWithReturn("api/logging", TestLogging.TEST_ENTRY, LogEntry.class);
        assertNotNull(newEntry, "Log Entry is not null");
        // Check if the log entry was created successfully
        LogEntry[] entries = HTTPRequests.get("api/logging?action=" + TestLogging.TEST_ENTRY.actionType, LogEntry[].class);
        boolean exists = false;
        for (LogEntry entry : entries)
            if (TestLogging.TEST_ENTRY.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Log Entry was added successfully");
    }

    @Test
    @Order(2)
    public void testGetLogEvent() throws IOException {
        LogEntry[] entries = HTTPRequests.get("api/logging?action=" + TestLogging.TEST_ENTRY.actionType, LogEntry[].class);
        boolean exists = false;
        for (LogEntry entry : entries)
            if (TestLogging.TEST_ENTRY.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Log Entry was added successfully");
    }

    @Test
    @Order(2)
    public void testUpdateLogEvent() throws IOException {
        TestLogging.TEST_ENTRY.x = 50000;
        HTTPRequests.put("logging", TestLogging.TEST_ENTRY);
        // Check if the log entry was updated successfully
        LogEntry[] entries = HTTPRequests.get("api/logging?action=" + TestLogging.TEST_ENTRY.actionType, LogEntry[].class);
        boolean exists = false;
        for (LogEntry entry : entries)
            if (TestLogging.TEST_ENTRY.equals(entry)) {
                exists = true;
                break;
            }
        assertTrue(exists, "Log Entry was updated successfully");
    }

    @Test
    @Order(3)
    public void testDeleteLogEvent() throws IOException {
        LogEntry deletedEntry = HTTPRequests.deleteWithReturn("api/logging", TestLogging.TEST_ENTRY, LogEntry.class);
        assertNotNull(deletedEntry, "Entry was deleted successfully");
        // Make sure the log event was deleted successfully
        LogEntry[] entries = HTTPRequests.get("api/logging?action=" + TestLogging.TEST_ENTRY.actionType, LogEntry[].class);
        boolean exists = false;
        for (LogEntry entry : entries)
            if (TestLogging.TEST_ENTRY.equals(entry)) {
                exists = true;
                break;
            }
        assertFalse(exists, "Log Entry was deleted successfully");
    }
}
