/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.tests.routes;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Ban;
import io.wurmatron.serveressentials.tests.sql.TestBans;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBanRoutes {

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
  public void testAddBanEntry() throws IOException {
    Ban newEntry = HTTPRequests.postWithReturn("api/ban", TestBans.TEST_BAN, Ban.class);
    TestBans.TEST_BAN.ban_id = newEntry.ban_id;
    assertNotNull(newEntry, "Entry was added successfully");
    // Make sure the ban was added.
  }

  @Test
  @Order(2)
  public void testGetBanEntry() throws IOException {
    Ban entry = HTTPRequests.get("api/ban/" + TestBans.TEST_BAN.ban_id, Ban.class);
    assertEquals(TestBans.TEST_BAN.ban_reason, entry.ban_reason, "Entry exists");
  }

  @Test
  @Order(2)
  public void testGetBans() throws IOException {
    Ban[] bans = HTTPRequests.get("api/ban", Ban[].class);
    boolean exists = false;
    for (Ban ban : bans)
      if (TestBans.TEST_BAN.ban_type.equals(ban.ban_type)) {
        exists = true;
        break;
      }
    assertTrue(exists, "Ban Entry Exists");
  }

  @Test
  @Order(2)
  public void testUpdateBan() throws IOException {
    TestBans.TEST_BAN.ban_reason = "Stupidity";
    HTTPRequests.put("api/ban/" + TestBans.TEST_BAN.ban_id, TestBans.TEST_BAN);
    // Make sure it was updated
    Ban entry = HTTPRequests.get("api/ban/" + TestBans.TEST_BAN.ban_id, Ban.class);
    assertEquals(TestBans.TEST_BAN.ban_reason, entry.ban_reason, "Entry was updated");
  }

  @Test
  @Order(3)
  public void testDeleteBan() throws IOException {
    Ban deletedBan =
        HTTPRequests.deleteWithReturn(
            "api/ban/" + TestBans.TEST_BAN.ban_id, TestBans.TEST_BAN, Ban.class);
    assertNotNull(deletedBan, "Ban was deleted");
  }
}
