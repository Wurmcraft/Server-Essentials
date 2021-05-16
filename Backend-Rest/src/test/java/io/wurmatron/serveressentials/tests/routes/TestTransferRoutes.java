package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.tests.sql.TestTransfers;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestTransferRoutes {

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.main(new String[]{});
        ServerEssentialsRest.config.server.host = "localhost";
        HTTPRequests.BASE_URL = "http://" + ServerEssentialsRest.config.server.host + ":" + ServerEssentialsRest.config.server.port + "/";
        // TODO Add Authentication or force config.testing when running tests
    }

    @Test
    @Order(1)
    public void testAddTransferEntry() throws IOException {
        TransferEntry createdEntry = HTTPRequests.postWithReturn("transfer", TestTransfers.TEST_ENTRY, TransferEntry.class);
        assertNotNull(createdEntry, "Transfer entry is not null");
        TestTransfers.TEST_ENTRY.transferID = createdEntry.transferID;
        assertEquals(TestTransfers.TEST_ENTRY, createdEntry, "Transfer Entries are the same");
        // Check if it was added
        createdEntry = HTTPRequests.get("transfer/" + createdEntry.transferID, TransferEntry.class);
        TestTransfers.TEST_ENTRY.transferID = createdEntry.transferID;
        assertNotNull(createdEntry);
        assertEquals(TestTransfers.TEST_ENTRY, createdEntry, "Transfer Entries are the same");
    }

    @Test
    @Order(2)
    public void testGetTransferEntry() throws IOException {
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID, TransferEntry.class);
        assertNotNull(entry, "Entry is not null");
        assertEquals(TestTransfers.TEST_ENTRY, entry, "Entries are the same");
    }

    @Test
    @Order(2)
    public void testGetUUID() throws IOException {
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID + "/uuid", TransferEntry.class);
        assertEquals(TestTransfers.TEST_ENTRY.uuid, entry.uuid, "UUID is the same");
    }

    @Test
    @Order(2)
    public void testGetStartTime() throws IOException {
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID + "/start-time", TransferEntry.class);
        assertEquals(TestTransfers.TEST_ENTRY.startTime, entry.startTime, "Start Time is the same");
    }

    @Test
    @Order(2)
    public void testGetItems() throws IOException {
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID + "/items", TransferEntry.class);
        assertArrayEquals(TestTransfers.TEST_ENTRY.items, entry.items, "Items are the same");
    }

    @Test
    @Order(2)
    public void testGetServerID() throws IOException {
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID + "/server-id", TransferEntry.class);
        assertEquals(TestTransfers.TEST_ENTRY.serverID, entry.serverID, "ServerID is the same");
    }

    @Test
    @Order(2)
    public void testUpdateEntry() throws IOException {
        TestTransfers.TEST_ENTRY.serverID = "Test2";
        HTTPRequests.put("transfer/" + TestTransfers.TEST_ENTRY.transferID, TestTransfers.TEST_ENTRY);
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID, TransferEntry.class);
        assertEquals(TestTransfers.TEST_ENTRY, entry, "Entries are the same");
    }

    @Test
    @Order(2)
    public void testPatchEntry() throws IOException {
        TestTransfers.TEST_ENTRY.uuid = "148cf139-dd14-4bf4-97a2-08305dfef0a9";
        HTTPRequests.patch("transfer/" + TestTransfers.TEST_ENTRY.transferID + "/uuid", TestTransfers.TEST_ENTRY);
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID, TransferEntry.class);
        assertEquals(TestTransfers.TEST_ENTRY.uuid, entry.uuid, "UUID are the same");
    }

    @Test
    @Order(3)
    public void testDeleteEntry() throws IOException {
        HTTPRequests.deleteWithReturn("transfer/" + TestTransfers.TEST_ENTRY.transferID, TestTransfers.TEST_ENTRY, TransferEntry.class);
        // Make sure entry was deleted
        TransferEntry entry = HTTPRequests.get("transfer/" + TestTransfers.TEST_ENTRY.transferID, TransferEntry.class);
        assertNull(entry, "Entry does not exist");
    }
}
