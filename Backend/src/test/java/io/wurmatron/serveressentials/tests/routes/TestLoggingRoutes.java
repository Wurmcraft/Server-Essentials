/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.tests.routes;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.LogEntry;
import io.wurmatron.serveressentials.tests.sql.TestLogging;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestLoggingRoutes {

  @BeforeAll
  public static void setup() throws IOException, SQLException {
    if (!isSetup) {
      ServerEssentialsRest.main(new String[] {});
      HTTPRequests.BASE_URL =
          "http://"
              + ServerEssentialsRest.config.server.host
              + ":"
              + ServerEssentialsRest.config.server.port
              + "/";
      // TODO Add Authentication or force config.testing when running tests
      isSetup = true;
    }
  }

  @Test
  @Order(1)
  public void testAddLoggingEvent() throws IOException {
    LogEntry newEntry =
        HTTPRequests.postWithReturn("api/logging", TestLogging.TEST_ENTRY, LogEntry.class);
    assertNotNull(newEntry, "Log Entry is not null");
    // Check if the log entry was created successfully
    LogEntry[] entries =
        HTTPRequests.get(
            "api/logging?action=" + TestLogging.TEST_ENTRY.action_type, LogEntry[].class);
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
    LogEntry[] entries =
        HTTPRequests.get(
            "api/logging?action=" + TestLogging.TEST_ENTRY.action_type, LogEntry[].class);
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
    TestLogging.TEST_ENTRY.action_data = "{\"block\": \"<minecraft:dirt\"}";
    HTTPRequests.put("api/logging", TestLogging.TEST_ENTRY);
    // Check if the log entry was updated successfully
    LogEntry[] entries =
        HTTPRequests.get(
            "api/logging?action=" + TestLogging.TEST_ENTRY.action_type, LogEntry[].class);
    boolean exists = false;
    for (LogEntry entry : entries)
      if (TestLogging.TEST_ENTRY.action_type.equals(entry.action_type)) {
        exists = true;
        break;
      }
    assertTrue(exists, "Log Entry was updated successfully");
  }

  @Test
  @Order(3)
  public void testDeleteLogEvent() throws IOException {
    LogEntry deletedEntry =
        HTTPRequests.deleteWithReturn("api/logging", TestLogging.TEST_ENTRY, LogEntry.class);
    assertNotNull(deletedEntry, "Entry was deleted successfully");
    // Make sure the log event was deleted successfully
    LogEntry[] entries =
        HTTPRequests.get(
            "api/logging?action=" + TestLogging.TEST_ENTRY.action_type, LogEntry[].class);
    boolean exists = false;
    for (LogEntry entry : entries)
      if (TestLogging.TEST_ENTRY.dim.equals(entry.dim)) {
        exists = true;
        break;
      }
    assertFalse(exists, "Log Entry was deleted successfully");
  }
}
