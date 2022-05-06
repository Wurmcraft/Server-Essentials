/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.tests.routes;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.TrackedStat;
import io.wurmatron.serveressentials.tests.sql.TestStatistics;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestStatisticRoutes {

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
  public void testAddStatistic() throws IOException {
    TrackedStat stat =
        HTTPRequests.postWithReturn("api/statistics", TestStatistics.TEST_STAT, TrackedStat.class);
    assertNotNull(stat, "Tracked Stat is not null");
    // Check for added entry
    TrackedStat[] stats =
        HTTPRequests.get(
            "api/statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
    boolean exists = false;
    for (TrackedStat s : stats)
      if (s.equals(stat)) {
        exists = true;
        break;
      }
    assertTrue(exists, "Entry was created");
  }

  @Test
  @Order(2)
  public void testGetStatistic() throws IOException {
    TrackedStat[] stats =
        HTTPRequests.get(
            "api/statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
    boolean exists = false;
    for (TrackedStat s : stats) if (s.equals(TestStatistics.TEST_STAT)) exists = true;
    assertTrue(exists, "Entry was created");
  }

  @Test
  @Order(2)
  public void testOverrideStatistic() throws IOException {
    TestStatistics.TEST_STAT.event_data = "{\"test\": true}";
    HTTPRequests.put("api/statistics", TestStatistics.TEST_STAT);
    // Check for update
    TrackedStat[] stats =
        HTTPRequests.get(
            "api/statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
    boolean exists = false;
    for (TrackedStat s : stats)
      if (s.event_type.equals(TestStatistics.TEST_STAT.event_type)) exists = true;
    assertTrue(exists, "Entry was Updated");
  }

  @Test
  @Order(3)
  public void testDeleteStatistic() throws IOException {
    TrackedStat deletedStat =
        HTTPRequests.deleteWithReturn(
            "api/statistics", TestStatistics.TEST_STAT, TrackedStat.class);
    assertNotNull(deletedStat, "Deleted Stat is returned");
    // Check if make sure it was deleted
    TrackedStat[] stats =
        HTTPRequests.get(
            "api/statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
    boolean exists = false;
    for (TrackedStat s : stats)
      if (s.equals(TestStatistics.TEST_STAT)) {
        exists = true;
        break;
      }
    assertFalse(exists, "Entry was Removed");
  }
}
