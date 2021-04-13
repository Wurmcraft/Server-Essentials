package io.wurmatron.serveressentials.sql.routes;

import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheTransfer;
import io.wurmatron.serveressentials.sql.cache_holder.CacheTransferUUID;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

public class SQLCacheTransfers extends SQLCache {

    public static final String TRANSFERS_TABLE = "transfers";

    private static final NonBlockingHashMap<String, CacheTransferUUID> uuidTransferCache = new NonBlockingHashMap<>();

    /**
     * Get a transfer entry based on its id
     *
     * @param transferID id for the given transfer id
     * @return instance of transfer entry
     */
    public static TransferEntry getTransferFromID(String transferID) {
        // Attempt to get from cache
        if (transferCache.containsKey(transferID))
            if (!needsUpdate(transferCache.get(transferID)))
                return transferCache.get(transferID).transferEntry;
            else
                transferCache.remove(transferID);
        // Not in cache / invalid
        try {
            TransferEntry entry = get("*", TRANSFERS_TABLE, "transferID", transferID, new TransferEntry());
            if (entry != null) {
                transferCache.put(transferID, new CacheTransfer(entry));
                return entry;
            }
        } catch (Exception e) {
            LOG.debug("Failed to find transfer with id '" + transferID + "' (" + e.getMessage() + ")");
        }
        // Transfer Entry, does not exist
        return null;
    }

    /**
     * Get a ist of transfer entries for the given uuid
     *
     * @param uuid user to lookup transfer entries for
     * @return a list of the given users transfer entries
     */
    public static List<TransferEntry> getTransferForUUID(String uuid) {
        // Attempt to get from cache
        if (uuidTransferCache.containsKey(uuid))
            if (!needsUpdate(uuidTransferCache.get(uuid)))
                return getTransfersFromIDs(uuidTransferCache.get(uuid).transferCacheEntrys);
            else
                uuidTransferCache.remove(uuid);
        // Not in cache / invalid
        try {
            List<TransferEntry> entries = getArray("*", TRANSFERS_TABLE, "uuid", uuid, new TransferEntry());
            if (entries.size() > 0) {
                // Convert and add to cache's
                List<String> cacheForUUID = new ArrayList<>();
                for (TransferEntry entry : entries) {
                    cacheForUUID.add(entry.transferID + "");
                    transferCache.put(entry.transferID + "", new CacheTransfer(entry));
                }
                uuidTransferCache.put(uuid, new CacheTransferUUID(cacheForUUID.toArray(new String[0])));
                return entries;
            }
        } catch (Exception e) {
            LOG.debug("Failed to find transfer id's for uuid '" + uuid + "' (" + e.getMessage() + ")");
        }
        return new ArrayList<>();
    }

    /**
     * Creates a array out of transferID's, converting the ID's into the real instance from DB or cache
     *
     * @param ids ids of transfer entries to convert into a list
     * @return the generated list of all the transfer entries
     */
    private static List<TransferEntry> getTransfersFromIDs(String[] ids) {
        List<TransferEntry> transfers = new ArrayList<>();
        for (String transferID : ids) {
            TransferEntry entry = getTransferFromID(transferID);
            if (entry != null)
                transfers.add(entry);
        }
        return transfers;
    }

    /**
     * Create a new transfer entry with the provided instance
     *
     * @param entry instance of the transfer entry to be created
     * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object)
     */
    public static void newTransferEntry(TransferEntry entry) {
        try {
            insert(TRANSFERS_TABLE, TRANSFERS_COLUMNS, entry);
            transferCache.put(entry.transferID + "", new CacheTransfer(entry));
        } catch (Exception e) {
            LOG.debug("Failed to add transfer id with id '" + entry.transferID + "' (" + e.getMessage() + ")");
            LOG.debug("TransferEntry: " + GSON.toJson(entry));
        }
    }

    /**
     * Updates a existing transfer entry with the provided info
     *
     * @param entry           transfer entry to collect the data to be updated
     * @param columnsToUpdate columns in the database to update with the provided data
     * @see SQLGenerator#update(String, String[], String, String, Object)
     */
    public static void updateTransfer(TransferEntry entry, String[] columnsToUpdate) {
        try {
            update(TRANSFERS_TABLE, columnsToUpdate, "transferID", entry.transferID + "", entry);
            if (transferCache.containsKey(entry.transferID + "")) {    // Exists in cache, updating
                try {
                    transferCache.get(entry.transferID + "").transferEntry = updateInfoLocal(columnsToUpdate, entry, transferCache.get(entry.transferID + "").transferEntry);
                    transferCache.get(entry.transferID + "").lastSync = System.currentTimeMillis();
                } catch (Exception e) {
                    LOG.debug("");
                }
            } else {    // Missing from cache
                transferCache.put(entry.transferID + "", new CacheTransfer(entry));
            }
        } catch (Exception e) {
            LOG.debug("Failed to update transfer entry with id '" + entry.transferID + "' (" + e.getMessage() + ")");
            LOG.debug("TransferEntry: " + GSON.toJson(entry));
        }
    }

    /**
     * Deletes the given transfer entry from the database
     * To remove from cache, but not delete use invalidate(uuid)
     *
     * @param transferID id of the transfer entry to be deleted
     * @see #invalidate(String)
     */
    public static void deleteTransfer(long transferID) {
        try {
            delete(TRANSFERS_TABLE, "transferID", transferID + "");
        } catch (Exception e) {
            LOG.debug("Failed to delete transferEntry with id '" + transferID + "' (" + e.getMessage() + ")");
        }
    }

    /**
     * Removes a entry from the cache, causing an update upon next request
     * Note: Does not delete anything from the database
     *
     * @param transferID id used for the transfer entry to remove from cache
     */
    public static void invalidate(String transferID) {
        transferCache.remove(transferID);
        LOG.debug("TransferEntry '" + transferID + " has been invalidated, will update on next request!");
    }

    /**
     * Removes all the entries related to the uuid of a user, causing an update upon next request
     * Note: Does not delete anything from the database
     *
     * @param uuid id used for the transfer id's to remove from cache
     */
    public static void invalidate(String uuid, Void _) {
        LOG.debug("TransferEntries for user '" + uuid + "' have been invalidated, will update on next request!");
        CacheTransferUUID uuidCache = uuidTransferCache.get(uuid);
        for (String id : uuidCache.transferCacheEntrys)
            invalidate(id);
        uuidTransferCache.remove(uuid);
    }

    /**
     * Cleanup the stored cache and look for expired entries
     */
    public static void cleanupCache() {
        LOG.info("Transfer Cache cleanup has begun!");
        // ID Cache
        List<String> toBeRemoved = new ArrayList<>();
        for (CacheTransfer entry : transferCache.values())
            if (needsUpdate(entry))
                toBeRemoved.add(entry.transferEntry.transferID + "");
        // UUID Cache
        List<String> toBeRemovedUUID = new ArrayList<>();
        for (String uuid : uuidTransferCache.keySet()) {
            CacheTransferUUID uuidCache = uuidTransferCache.get(uuid);
            if (needsUpdate(uuidCache))
                toBeRemovedUUID.add(uuid);
        }
        // Remove from cache
        int count = 0;
        for (String transferEntry : toBeRemoved) {
            count++;
            invalidate(transferEntry);
        }
        for (String uuid : toBeRemovedUUID) {
            count += uuidTransferCache.get(uuid).transferCacheEntrys.length;
            invalidate(uuid, null);
        }
        LOG.info("Transfer Cache has been cleaned, " + count + " entries have been removed!");
    }

    /**
     * Removes the expired entries from the database
     */
    public static void cleanupDB() {
        LOG.info("Cleanup on DB table, '" + TRANSFERS_TABLE + "' has started!");
        try {
            int count = 0;
            List<TransferEntry> dbData = getAll("transferID, startTime", TRANSFERS_TABLE, new TransferEntry());
            for (TransferEntry entry : dbData)
                if (entry.startTime + 10000 < Instant.now().getEpochSecond()) {
                    count++;
                    deleteTransfer(entry.transferID);
                }
            LOG.info("Transfer DB has been cleaned, " + count + " entries have been removed!");
            return;
        } catch (Exception e) {
            LOG.debug("Failed to collect transfer entries from the database '(" + e.getMessage() + ")");
        }
        LOG.warn("Failed to cleanup DB table '" + TRANSFERS_TABLE + "'");
    }
}
