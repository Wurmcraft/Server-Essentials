/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import io.wurmatron.serveressentials.models.TransferEntry;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.cache_holder.CacheTransfer;
import io.wurmatron.serveressentials.sql.cache_holder.CacheTransferUUID;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class SQLCacheTransfers extends SQLCache {

  public static final String TRANSFERS_TABLE = "transfers";

  private static final NonBlockingHashMap<String, CacheTransferUUID> uuidTransferCache =
      new NonBlockingHashMap<>();

  /**
   * Get a transfer entry based on its id
   *
   * @param transferID id for the given transfer id
   * @return instance of transfer entry
   */
  @Nullable
  public static TransferEntry getID(long transferID) {
    // Attempt to get from cache
    if (transferCache.containsKey(transferID))
      if (!needsUpdate(transferCache.get(transferID)))
        return transferCache.get(transferID).transferEntry;
      else transferCache.remove(transferID);
    // Not in cache / invalid
    try {
      TransferEntry entry =
          get("*", TRANSFERS_TABLE, "transfer_id", "" + transferID, new TransferEntry());
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
  public static List<TransferEntry> getUUID(String uuid) {
    // Attempt to get from cache
    if (uuidTransferCache.containsKey(uuid))
      if (!needsUpdate(uuidTransferCache.get(uuid)))
        return getTransfersFromIDs(
            convertToLongArr(uuidTransferCache.get(uuid).transferCacheEntrys));
      else uuidTransferCache.remove(uuid);
    // Not in cache / invalid
    try {
      List<TransferEntry> entries =
          getArray("*", TRANSFERS_TABLE, "uuid", uuid, new TransferEntry());
      if (entries.size() > 0) {
        // Convert and add to cache's
        List<String> cacheForUUID = new ArrayList<>();
        for (TransferEntry entry : entries) {
          cacheForUUID.add(entry.transfer_id + "");
          transferCache.put(entry.transfer_id, new CacheTransfer(entry));
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
   * Converts a string of numbers into an array of longs
   *
   * @param arr array of longs but in string format
   * @return Converted array of string to long
   */
  private static long[] convertToLongArr(String[] arr) {
    long[] longArr = new long[arr.length];
    for (int x = 0; x < arr.length; x++) longArr[x] = Long.parseLong(arr[x]);
    return longArr;
  }

  /**
   * Creates a array out of transferID's, converting the ID's into the real instance from DB or
   * cache
   *
   * @param ids ids of transfer entries to convert into a list
   * @return the generated list of all the transfer entries
   */
  private static List<TransferEntry> getTransfersFromIDs(long[] ids) {
    List<TransferEntry> transfers = new ArrayList<>();
    for (long transferID : ids) {
      TransferEntry entry = getID(transferID);
      if (entry != null) transfers.add(entry);
    }
    return transfers;
  }

  /**
   * Create a new transfer entry with the provided instance
   *
   * @param entry instance of the transfer entry to be created
   * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object, boolean)
   */
  @Nullable
  public static TransferEntry create(TransferEntry entry) {
    try {
      entry.transfer_id =
          (long)
              insert(
                  TRANSFERS_TABLE,
                  Arrays.copyOfRange(TRANSFERS_COLUMNS, 1, TRANSFERS_COLUMNS.length),
                  entry,
                  true);
      transferCache.put(entry.transfer_id, new CacheTransfer(entry));
      return entry;
    } catch (Exception e) {
      LOG.debug(
          "Failed to add transfer id with id '" + entry.transfer_id + "' (" + e.getMessage() + ")");
      LOG.debug("TransferEntry: " + GSON.toJson(entry));
    }
    return null;
  }

  /**
   * Updates a existing transfer entry with the provided info
   *
   * @param entry transfer entry to collect the data to be updated
   * @param columnsToUpdate columns in the database to update with the provided data
   * @see io.wurmatron.serveressentials.sql.SQLGenerator#update(String, String[], String, String,
   *     Object)
   */
  public static boolean update(TransferEntry entry, String[] columnsToUpdate) {
    try {
      update(TRANSFERS_TABLE, columnsToUpdate, "transfer_id", entry.transfer_id + "", entry);
      if (transferCache.containsKey(entry.transfer_id)) { // Exists in cache, updating
        transferCache.get(entry.transfer_id).transferEntry =
            updateInfoLocal(
                columnsToUpdate, entry, transferCache.get(entry.transfer_id).transferEntry);
        transferCache.get(entry.transfer_id).lastSync = System.currentTimeMillis();
      } else { // Missing from cache
        transferCache.put(entry.transfer_id, new CacheTransfer(entry));
      }
      return true;
    } catch (Exception e) {
      LOG.debug(
          "Failed to update transfer entry with id '"
              + entry.transfer_id
              + "' ("
              + e.getMessage()
              + ")");
      LOG.debug("TransferEntry: " + GSON.toJson(entry));
    }
    return false;
  }

  /**
   * Deletes the given transfer entry from the database To remove from cache, but not delete use
   * invalidate(uuid)
   *
   * @param transferID id of the transfer entry to be deleted
   * @see #invalidate(String)
   */
  public static boolean delete(long transferID) {
    try {
      delete(TRANSFERS_TABLE, "transfer_id", transferID + "");
      invalidate(transferID);
      return true;
    } catch (Exception e) {
      LOG.debug(
          "Failed to delete transferEntry with id '" + transferID + "' (" + e.getMessage() + ")");
    }
    return false;
  }

  /**
   * Removes a entry from the cache, causing an update upon next request Note: Does not delete
   * anything from the database
   *
   * @param transferID id used for the transfer entry to remove from cache
   */
  public static void invalidate(long transferID) {
    transferCache.remove(transferID);
    LOG.debug(
        "TransferEntry '" + transferID + " has been invalidated, will update on next request!");
  }

  /**
   * Removes all the entries related to the uuid of a user, causing an update upon next request
   * Note: Does not delete anything from the database
   *
   * @param uuid id used for the transfer id's to remove from cache
   */
  public static void invalidate(String uuid) {
    LOG.debug(
        "TransferEntries for user '"
            + uuid
            + "' have been invalidated, will update on next request!");
    CacheTransferUUID uuidCache =
        uuidTransferCache.getOrDefault(uuid, new CacheTransferUUID(new String[] {}));
    for (String id : uuidCache.transferCacheEntrys) invalidate(id);
    uuidTransferCache.remove(uuid);
  }

  /** Cleanup the stored cache and look for expired entries */
  public static void cleanupCache() {
    LOG.info("Transfer Cache cleanup has begun!");
    // ID Cache
    List<Long> toBeRemoved = new ArrayList<>();
    for (CacheTransfer entry : transferCache.values())
      if (needsUpdate(entry)) toBeRemoved.add(entry.transferEntry.transfer_id);
    // UUID Cache
    List<String> toBeRemovedUUID = new ArrayList<>();
    for (String uuid : uuidTransferCache.keySet()) {
      CacheTransferUUID uuidCache = uuidTransferCache.get(uuid);
      if (needsUpdate(uuidCache)) toBeRemovedUUID.add(uuid);
    }
    // Remove from cache
    int count = 0;
    for (Long transferEntry : toBeRemoved) {
      count++;
      invalidate(transferEntry);
    }
    for (String uuid : toBeRemovedUUID) {
      count += uuidTransferCache.get(uuid).transferCacheEntrys.length;
      invalidate(uuid);
    }
    LOG.info("Transfer Cache has been cleaned, " + count + " entries have been removed!");
  }

  /** Removes the expired entries from the database */
  public static void cleanupDB() {
    LOG.debug("Cleanup on DB table, '" + TRANSFERS_TABLE + "' has started!");
    try {
      int count = 0;
      List<TransferEntry> dbData =
          getAll("transfer_id, start_time", TRANSFERS_TABLE, new TransferEntry());
      for (TransferEntry entry : dbData)
        if (entry.start_time + 10000 < Instant.now().getEpochSecond()) {
          count++;
          delete(entry.transfer_id);
        }
      LOG.debug("Transfer DB has been cleaned, " + count + " entries have been removed!");
      return;
    } catch (Exception e) {
      LOG.debug("Failed to collect transfer entries from the database '(" + e.getMessage() + ")");
    }
    LOG.warn("Failed to cleanup DB table '" + TRANSFERS_TABLE + "'");
  }
}
