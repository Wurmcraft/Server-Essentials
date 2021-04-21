package io.wurmatron.serveressentials.tests.sql;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.models.transfer.ItemWrapper;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.routes.SQLCacheAccount;
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
        TransferEntry entry = SQLCacheTransfers.newTransferEntry(TEST_ENTRY);
        assertNotNull(entry, "Transfer Entry has been successfully created without errors");
        // Check if it was added
        entry = SQLCacheTransfers.getTransferFromID(TEST_ENTRY.transferID);
        assertEquals(TEST_ENTRY, entry, "Added entry should be the same as the one saved.");
        // Find transferID
        SQLCacheTransfers.invalidate(entry.transferID);
        entry = SQLCacheTransfers.getTransferFromID(TEST_ENTRY.transferID);
        assertEquals(TEST_ENTRY, entry, "Entry should be the same as the one saved.");
    }

    @Test
    @Order(2)
    public void testUpdateTransfer() {
        TEST_ENTRY.serverID = "Test2";
        boolean updated = SQLCacheTransfers.updateTransfer(TEST_ENTRY, new String[]{"serverID"});
        assertTrue(updated, " has been successfully created without errors");
        // Invalidate Cache and try again
        SQLCacheTransfers.invalidate(TEST_ENTRY.transferID);
        TransferEntry entry = SQLCacheTransfers.getTransferFromID(TEST_ENTRY.transferID);
        assertNotNull(entry, "Transfer Entry Exists");
        assertEquals(TEST_ENTRY.serverID, entry.serverID);
    }

    @Test
    @Order(2)
    public void testGetTransferID() {
        TransferEntry transferEntry = SQLCacheTransfers.getTransferFromID(TEST_ENTRY.transferID);
        assertEquals(TEST_ENTRY, transferEntry, "The TransferEntry are equal");
        // Remove from cache and try again
        SQLCacheTransfers.invalidate(TEST_ENTRY.transferID);
        transferEntry = SQLCacheTransfers.getTransferFromID(TEST_ENTRY.transferID);
        // Test Transfer Entry
        assertNotNull(transferEntry, "Cache has the Transfer Entry");
        assertEquals(TEST_ENTRY.serverID, transferEntry.serverID, "ServerID are the same");
        assertEquals(TEST_ENTRY.uuid, transferEntry.uuid, "uuid are the same");
        assertArrayEquals(TEST_ENTRY.items, transferEntry.items, "items are the same");
    }

    @Test
    @Order(2)
    public void testGetTransferUUID() {
        long TEST_ID = TEST_ENTRY.transferID;
        TEST_ENTRY.transferID = -1;
        TransferEntry newEntry = SQLCacheTransfers.newTransferEntry(TEST_ENTRY);
        TEST_ENTRY.transferID = TEST_ID;
        assertNotNull(newEntry, "New entry should not be null");
        List<TransferEntry> entries = SQLCacheTransfers.getTransferForUUID(TEST_ENTRY.uuid);
        assertTrue(entries.size() >= 2, "Should be at least 2 entries");
        // Remove from cache and try again
        SQLCacheTransfers.invalidate(TEST_ENTRY.uuid);
        entries = SQLCacheTransfers.getTransferForUUID(TEST_ENTRY.uuid);
        assertTrue(entries.size() >= 2, "Should be at least 2 entries");
    }

    @Test
    @Order(3)
    public void testDeleteTransferID() {
        boolean deleted = SQLCacheTransfers.deleteTransfer(TEST_ENTRY.transferID);
        assertTrue(deleted, " has been successfully deleted without errors");
        // Make sure its deleted
        TransferEntry entry = SQLCacheTransfers.getTransferFromID(TEST_ENTRY.transferID);
        assertNull(entry, "Entry has been successfully deleted");
        // Delete any still existing transfers
        List<TransferEntry> entries = SQLCacheTransfers.getTransferForUUID(TEST_ENTRY.uuid);
        for (TransferEntry e : entries)
            assertTrue(SQLCacheTransfers.deleteTransfer(e.transferID), "Entry has been successfully deleted");
    }
}
