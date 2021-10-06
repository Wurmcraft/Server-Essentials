package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.models.transfer.ItemWrapper;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheTransfers;
import io.wurmatron.serveressentials.utils.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestTransfers {

    public static final TransferEntry TEST_ENTRY = new TransferEntry(0, "d8d5a923-7b20-43d8-883b-1150148d6955",
            Instant.now().toEpochMilli(), new ItemWrapper[]{new ItemWrapper("<minecraft:diamond>"), new ItemWrapper("<61xminecraft:dirt@5>")}, "Test");

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.config = ConfigLoader.setupAndHandleConfig();
        ServerEssentialsRest.dbConnection = SQLGenerator.create();
    }

    @Test
    @Order(1)
    public void testAddTransfer() {
        TransferEntry entry = SQLCacheTransfers.create(TEST_ENTRY);
        assertNotNull(entry, "Transfer Entry has been successfully created without errors");
        // Check if it was added
        entry = SQLCacheTransfers.getID(TEST_ENTRY.transfer_id);
        assertEquals(TEST_ENTRY, entry, "Added entry should be the same as the one saved.");
        // Find transferID
        SQLCacheTransfers.invalidate(entry.transfer_id);
        entry = SQLCacheTransfers.getID(TEST_ENTRY.transfer_id);
        assertEquals(TEST_ENTRY, entry, "Entry should be the same as the one saved.");
    }

    @Test
    @Order(2)
    public void testUpdateTransfer() {
        TEST_ENTRY.server_id = "Test2";
        boolean updated = SQLCacheTransfers.update(TEST_ENTRY, new String[]{"serverID"});
        assertTrue(updated, " has been successfully created without errors");
        // Invalidate Cache and try again
        SQLCacheTransfers.invalidate(TEST_ENTRY.transfer_id);
        TransferEntry entry = SQLCacheTransfers.getID(TEST_ENTRY.transfer_id);
        assertNotNull(entry, "Transfer Entry Exists");
        assertEquals(TEST_ENTRY.server_id, entry.server_id);
    }

    @Test
    @Order(2)
    public void testGetTransferID() {
        TransferEntry transferEntry = SQLCacheTransfers.getID(TEST_ENTRY.transfer_id);
        assertEquals(TEST_ENTRY, transferEntry, "The TransferEntry are equal");
        // Remove from cache and try again
        SQLCacheTransfers.invalidate(TEST_ENTRY.transfer_id);
        transferEntry = SQLCacheTransfers.getID(TEST_ENTRY.transfer_id);
        // Test Transfer Entry
        assertNotNull(transferEntry, "Cache has the Transfer Entry");
        assertEquals(TEST_ENTRY.server_id, transferEntry.server_id, "ServerID are the same");
        assertEquals(TEST_ENTRY.uuid, transferEntry.uuid, "uuid are the same");
        assertArrayEquals(TEST_ENTRY.items, transferEntry.items, "items are the same");
    }

    @Test
    @Order(2)
    public void testGetTransferUUID() {
        long TEST_ID = TEST_ENTRY.transfer_id;
        TEST_ENTRY.transfer_id = -1L;
        TransferEntry newEntry = SQLCacheTransfers.create(TEST_ENTRY);
        TEST_ENTRY.transfer_id = TEST_ID;
        assertNotNull(newEntry, "New entry should not be null");
        List<TransferEntry> entries = SQLCacheTransfers.getUUID(TEST_ENTRY.uuid);
        assertTrue(entries.size() >= 2, "Should be at least 2 entries");
        // Remove from cache and try again
        SQLCacheTransfers.invalidate(TEST_ENTRY.uuid);
        entries = SQLCacheTransfers.getUUID(TEST_ENTRY.uuid);
        assertTrue(entries.size() >= 2, "Should be at least 2 entries");
    }

    @Test
    @Order(3)
    public void testDeleteTransferID() {
        boolean deleted = SQLCacheTransfers.delete(TEST_ENTRY.transfer_id);
        assertTrue(deleted, " has been successfully deleted without errors");
        // Make sure its deleted
        TransferEntry entry = SQLCacheTransfers.getID(TEST_ENTRY.transfer_id);
        assertNull(entry, "Entry has been successfully deleted");
        // Delete any still existing transfers
        List<TransferEntry> entries = SQLCacheTransfers.getUUID(TEST_ENTRY.uuid);
        for (TransferEntry e : entries)
            assertTrue(SQLCacheTransfers.delete(e.transfer_id), "Entry has been successfully deleted");
    }
}
