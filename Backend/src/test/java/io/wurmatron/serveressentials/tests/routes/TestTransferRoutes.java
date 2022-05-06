/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.tests.routes;

import static io.wurmatron.serveressentials.tests.utils.Tests.isSetup;
import static org.junit.jupiter.api.Assertions.*;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.models.transfer.ItemWrapper;
import io.wurmatron.serveressentials.tests.sql.TestTransfers;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import java.io.IOException;
import java.sql.SQLException;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestTransferRoutes {

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
  public void testAddTransferEntry() throws IOException {
    TransferEntry createdEntry =
        HTTPRequests.postWithReturn("api/transfer", TestTransfers.TEST_ENTRY, TransferEntry.class);
    assertNotNull(createdEntry, "Transfer entry is not null");
    TestTransfers.TEST_ENTRY.transfer_id = createdEntry.transfer_id;
    assertEquals(TestTransfers.TEST_ENTRY, createdEntry, "Transfer Entries are the same");
    // Check if it was added
    createdEntry =
        HTTPRequests.get("api/transfer/" + createdEntry.transfer_id, TransferEntry.class);
    TestTransfers.TEST_ENTRY.transfer_id = createdEntry.transfer_id;
    assertNotNull(createdEntry);
    assertEquals(TestTransfers.TEST_ENTRY, createdEntry, "Transfer Entries are the same");
  }

  @Test
  @Order(2)
  public void testGetTransferEntry() throws IOException {
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id, TransferEntry.class);
    assertNotNull(entry, "Entry is not null");
    assertEquals(TestTransfers.TEST_ENTRY, entry, "Entries are the same");
  }

  @Test
  @Order(2)
  public void testGetUUID() throws IOException {
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id + "/uuid", TransferEntry.class);
    assertEquals(TestTransfers.TEST_ENTRY.uuid, entry.uuid, "UUID is the same");
  }

  @Test
  @Order(2)
  public void testGetStartTime() throws IOException {
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id + "/start-time",
            TransferEntry.class);
    assertEquals(TestTransfers.TEST_ENTRY.start_time, entry.start_time, "Start Time is the same");
  }

  @Test
  @Order(2)
  public void testGetItems() throws IOException {
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id + "/items", TransferEntry.class);
    assertArrayEquals(TestTransfers.TEST_ENTRY.items, entry.items, "Items are the same");
  }

  @Test
  @Order(2)
  public void testGetServerID() throws IOException {
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id + "/server-id",
            TransferEntry.class);
    String serverID = entry.server_id;
    assertEquals(TestTransfers.TEST_ENTRY.server_id, serverID, "ServerID is the same");
  }

  @Test
  @Order(2)
  public void testUpdateEntry() throws IOException {
    TestTransfers.TEST_ENTRY.items = new ItemWrapper[] {new ItemWrapper("<minecraft:apple>")};
    HTTPRequests.put(
        "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id, TestTransfers.TEST_ENTRY);
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id, TransferEntry.class);
    assertEquals(entry, TestTransfers.TEST_ENTRY, "Entries are the same");
  }

  @Test
  @Order(2)
  public void testPatchEntry() throws IOException {
    TestTransfers.TEST_ENTRY.items = new ItemWrapper[] {new ItemWrapper("<minecraft:apple>")};
    HTTPRequests.patch(
        "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id + "/items",
        TestTransfers.TEST_ENTRY);
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id, TransferEntry.class);
    assertArrayEquals(TestTransfers.TEST_ENTRY.items, entry.items, "Items are the same");
  }

  @Test
  @Order(3)
  public void testDeleteEntry() throws IOException {
    HTTPRequests.deleteWithReturn(
        "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id,
        TestTransfers.TEST_ENTRY,
        TransferEntry.class);
    // Make sure entry was deleted
    TransferEntry entry =
        HTTPRequests.get(
            "api/transfer/" + TestTransfers.TEST_ENTRY.transfer_id, TransferEntry.class);
    assertNull(entry, "Entry does not exist");
  }
}
