/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import io.wurmatron.serveressentials.models.MarketEntry;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheMarket;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SQLCacheMarket extends SQLCache {

  public static final String MARKET_TABLE = "markets";

  /**
   * @param serverID id of the server the market entry exists on
   * @param sellerUUID uuid of the server that created this entry
   * @return instance of all the market entries related to the server and seller
   * @see SQLGenerator#getArray(String, String, String, String, Object) (String, String, String,
   *     String, Object)
   */
  public static List<MarketEntry> get(String serverID, String sellerUUID) {
    if (marketCache.containsKey(serverID)) {
      List<MarketEntry> sellerEntries = new ArrayList<>();
      for (CacheMarket entry : marketCache.get(serverID)) {
        if (needsUpdate(entry)) { // Updated at same time
          invalidate(serverID);
          break;
        }
        if (entry.marketEntry.seller_uuid.equals(sellerUUID)) {
          sellerEntries.add(entry.marketEntry.clone());
        }
        return sellerEntries;
      }
      // Missing / invalid
      List<MarketEntry> serverEntries = getServer(serverID);
      List<CacheMarket> serverCache = new ArrayList<>();
      if (serverEntries != null && serverEntries.size() > 0) {
        // Add to cache
        for (MarketEntry entry : serverEntries) {
          CacheMarket marketEntry = new CacheMarket(entry);
          serverCache.add(marketEntry);
        }
        marketCache.put(serverID, serverCache);
      }
      return serverEntries;
    }
    return new ArrayList<>();
  }

  /**
   * Returns a list of all the market entries instances within the db, that are from the requested
   * server, if any exist
   *
   * @param serverID id of the server to look for market entries
   * @return instance of all the market entries related to the provided server
   * @see SQLGenerator#getArray(String, String, String, String, Object)
   */
  public static List<MarketEntry> getServer(String serverID) {
    if (marketCache.containsKey(serverID)) {
      List<MarketEntry> entries = new ArrayList<>();
      for (CacheMarket entry : marketCache.get(serverID)) {
        if (!needsUpdate(entry)) {
          entries.add(entry.marketEntry);
        } else {
          invalidate(serverID);
          break;
        }
      }
      if (entries.size() == marketCache.get(serverID).size()) {
        return entries;
      }
    }
    // Missing / invalid
    try {
      List<MarketEntry> sqlEntries =
          getArray("*", MARKET_TABLE, "server_id", serverID, new MarketEntry());
      List<CacheMarket> sqlCache = new ArrayList<>();
      for (MarketEntry entry : sqlEntries) {
        sqlCache.add(new CacheMarket(entry.clone()));
      }
      return sqlEntries;
    } catch (Exception e) {
      LOG.debug(
          "Failed to get Market Entries for serverID '" + serverID + "' ("
              + e.getMessage() + ")");
    }
    return null;
  }

  /**
   * Returns a list of all the market entries instances within the db
   *
   * <p>Note: This method does not use / contain a cache
   *
   * @return instance of all the market entries in the db, that are of this type, if any exists
   * @see SQLGenerator#getArray(String, String, String, String, Object)
   */
  public static List<MarketEntry> get() {
    try {
      return getAll("*", MARKET_TABLE, new MarketEntry());
    } catch (Exception e) {
      LOG.debug("Failed to get Market Entries (" + e.getMessage() + ")");
    }
    return new ArrayList<>();
  }

  /**
   * Creates a new market entry in the db
   *
   * @param entry instance of the market entry to be created
   * @return returns the same object, that was provided
   * @see SQLGenerator#insert(String, String[], Object, boolean)
   */
  @Nullable
  public static MarketEntry create(MarketEntry entry) {
    try {
      insert(MARKET_TABLE, MARKETS_COLUMNS, entry, false);
      if (marketCache.containsKey(entry.server_id)) {
        marketCache.get(entry.server_id).add(new CacheMarket(entry));
      } else {
        List<CacheMarket> newEntry = new ArrayList<>();
        newEntry.add(new CacheMarket(entry));
        marketCache.put(entry.server_id, newEntry);
      }
      return entry;
    } catch (Exception e) {
      LOG.debug(
          "Failed to create new market entry for UUID '"
              + entry.seller_uuid
              + "' on server '"
              + entry.server_id
              + "' ("
              + e.getMessage()
              + ")");
      LOG.debug("MarketEntry: " + GSON.toJson(entry));
    }
    return null;
  }

  /**
   * Updates a market entry in the db
   *
   * @param entry market entry to be updated
   * @return if the update was successful
   * @see SQLGenerator#update(String, String[], String, String, Object)
   */
  public static boolean update(MarketEntry entry, String[] columnsToUpdate) {
    try {
      update(
          MARKET_TABLE,
          columnsToUpdate,
          new String[]{"seller_uuid", "server_id", "timestamp", "market_type"},
          new String[]{
              entry.seller_uuid, entry.server_id, "" + entry.timestamp, entry.market_type
          },
          entry);
      // Update Cache
      List<CacheMarket> updatedMarketEntries = new ArrayList<>();
      for (CacheMarket cache : marketCache.get(entry.server_id)) {
        if (cache.marketEntry.seller_uuid.equals(entry.seller_uuid)
            && cache.marketEntry.server_id.equals(entry.server_id)
            && cache.marketEntry.timestamp == entry.timestamp
            && cache.marketEntry.market_type.equals(entry.market_type)) {
          updateInfoLocal(columnsToUpdate, entry, cache.marketEntry);
        }
        updatedMarketEntries.add(cache);
      }
      marketCache.put(entry.server_id, updatedMarketEntries);
      return true;
    } catch (Exception e) {
      LOG.debug(
          "Failed to update MarketEntry for UUID '"
              + entry.seller_uuid
              + "' on server '"
              + entry.server_id
              + "' ("
              + e.getMessage()
              + ")");
      LOG.debug("MarketEntry: " + GSON.toJson(entry));
    }
    return false;
  }

  /**
   * Deletes / Removes a Market Entry from the db
   *
   * @param serverID serverID of the given market entry
   * @param uuid uuid of the user that created the given entry
   * @param timestamp timestamp when the entry was created
   * @return if the given entry was successfully deleted
   */
  public static boolean delete(String serverID, String uuid, long timestamp) {
    try {
      delete(
          MARKET_TABLE,
          new String[]{"server_id", "seller_uuid", "timestamp"},
          new String[]{serverID, uuid, "" + timestamp});
      invalidate(serverID);
      return true;
    } catch (Exception e) {
      LOG.debug(
          "Failed to delete market entry for UUID '"
              + uuid
              + "' on server '"
              + serverID
              + "' ("
              + e.getMessage()
              + ")");
    }
    return false;
  }

  /**
   * Invalidates / removes the the given entries
   *
   * @param serverID name of the server that contains the entries
   */
  public static void invalidate(String serverID) {
    LOG.debug(
        "Market Entries on '"
            + serverID
            + "' have been invalidated, will update upon next request!");
    marketCache.remove(serverID);
  }

  /** Run periodically to cleanup the cache and remove expired / invalid entries */
  public static void cleanupCache() {
    LOG.debug("Market Cache cleanup has begun!");
    List<String> toBeRemoved = new ArrayList<>();
    for (String key : marketCache.keySet()) {
      for (CacheMarket entry : marketCache.get(key)) {
        if (needsUpdate(entry)) {
          toBeRemoved.add(key);
          break;
        }
      }
    }
    // Remove from cache
    int count = 0;
    for (String serverID : toBeRemoved) {
      count += marketCache.get(serverID).size();
      invalidate(serverID);
    }
    LOG.debug("Market Cache has been cleaned, " + count + " entries have been removed!");
  }

  /** Run periodically to cleanup the db and remove expired / invalid entries */
  // TODO Implement Market Entry Timeout
  public static void cleanupDB() {
  }
}
