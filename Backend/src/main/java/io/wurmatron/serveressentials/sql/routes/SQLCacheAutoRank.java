/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import io.wurmatron.serveressentials.models.AutoRank;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheAutoRank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public class SQLCacheAutoRank extends SQLCache {

  public static final String AUTORANK_TABLE = "autoranks";

  /**
   * Get a auto rank based on the current rank
   *
   * @param currentRank name of rank that is part of the rank up
   * @return instance of autorank
   * @see SQLGenerator#get(String, String, String, String, Object)
   * @see #invalidate(String)
   */
  @Nullable
  public static AutoRank get(String currentRank) {
    // Attempt to get from cache
    if (autoRankCache.contains(currentRank.toUpperCase())) {
      if (!needsUpdate(autoRankCache.get(currentRank.toUpperCase()))) {
        return autoRankCache.get(currentRank.toUpperCase()).autoRank;
      } else {
        autoRankCache.remove(currentRank.toUpperCase());
      }
    }
    // Not in cache / invalid
    try {
      AutoRank autoRank = get("*", AUTORANK_TABLE, "rank", currentRank, new AutoRank());
      if (autoRank != null) {
        autoRankCache.put(autoRank.rank.toUpperCase(), new CacheAutoRank(autoRank));
        return autoRank;
      }
    } catch (Exception e) {
      LOG.debug(
          "Failed to find autorank with name '" + currentRank + "' + ('" + e.getMessage()
              + "')");
    }
    // Auto Rank does not exist
    return null;
  }

  /**
   * Creates a new auto rank
   *
   * @param autoRank instance to be created
   * @return auto rank instance with all information filled out
   * @see SQLGenerator#insert(String, String[], Object, boolean)
   */
  @Nullable
  public static AutoRank create(AutoRank autoRank) {
    try {
      insert(AUTORANK_TABLE, AUTORANKS_COLUMNS, autoRank, false);
      autoRankCache.put(autoRank.rank.toUpperCase(), new CacheAutoRank(autoRank));
      return autoRank;
    } catch (Exception e) {
      LOG.debug(
          "Failed to add autorank with name '" + autoRank.rank + "' (" + e.getMessage()
              + ")");
      LOG.debug("AutoRank: " + GSON.toJson(autoRank));
    }
    return null;
  }

  /**
   * Updates the specific auto rank information
   *
   * @param autoRank instance of autorank to pull the updated rows from
   * @param columnsToUpdate values to update in the SQL
   * @return if the auto rank has been successfully updated
   * @see SQLGenerator#update(String, String[], String, String, Object)
   */
  public static boolean update(AutoRank autoRank, String[] columnsToUpdate) {
    try {
      update(AUTORANK_TABLE, columnsToUpdate, "rank", "" + autoRank.rank, autoRank);
      if (autoRankCache.containsKey(autoRank.rank.toUpperCase())) {
        try {
          autoRankCache.get(autoRank.rank.toUpperCase()).autoRank =
              updateInfoLocal(
                  columnsToUpdate,
                  autoRank,
                  autoRankCache.get(autoRank.rank.toUpperCase()).autoRank);
          autoRankCache.get(
              autoRank.rank.toUpperCase()).lastSync = System.currentTimeMillis();
          return true;
        } catch (Exception e) {
          LOG.debug(
              "Failed to update autorank with name '"
                  + autoRank.rank
                  + "' ("
                  + e.getMessage()
                  + ")");
          LOG.debug("AutoRank: " + GSON.toJson(autoRank));
        }
      } else {
        autoRankCache.put(autoRank.rank.toUpperCase(), new CacheAutoRank(autoRank));
        return true;
      }
    } catch (Exception e) {
      LOG.debug(
          "Failed to update autorank with name '" + autoRank.rank + "' (" + e.getMessage()
              + ")");
      LOG.debug("AutoRank: " + GSON.toJson(autoRank));
    }
    return false;
  }

  /**
   * Gets a list of all the auto-ranks
   *
   * @return a list of all the auto-ranks
   * @see SQLGenerator#getArray(String, String, String, String, Object)
   */
  public static List<AutoRank> get() {
    try {
      return SQLDirect.queryArray("SELECT * FROM " + AUTORANK_TABLE, new AutoRank());
    } catch (Exception e) {
      LOG.debug("Failed to GET autoranks (" + e.getMessage() + ")");
    }
    return new ArrayList<>();
  }

  /**
   * Deletes a specific AutoRank based on its the users current rank
   *
   * @param rank Name of the autorank to be deleted
   * @return if the given autorank has been deleted
   * @see #invalidate(String)
   */
  public static boolean delete(String rank) {
    try {
      delete(AUTORANK_TABLE, "rank", rank + "");
      invalidate(rank.toUpperCase());
      return true;
    } catch (Exception e) {
      LOG.debug("Failed to delete autorank with name '" + rank + "'");
    }
    return false;
  }

  /**
   * Removes a entry from the cache, causing an update upon next request Note: Does not delete
   * anything from the database
   *
   * @param rank name id used for autorank to be remove from cache
   */
  public static void invalidate(String rank) {
    autoRankCache.remove(rank);
    LOG.debug(
        "Auto-Rank '" + rank + " has been invalidated, will update on next request!");
  }

  /**
   * Removes a entry from the cache, causing an update upon next request Note: Does not delete
   * anything from the database
   *
   * @param autorankID id of the autorank
   */
  public static void invalidate(long autorankID) {
    autoRankCache.remove(autorankID);
    LOG.debug("AutoRank '" + autorankID
        + "' has been invalidated, will update on next request!");
  }

  /** Cleanup the stored cache and look for expired entries */
  public static void cleanupCache() {
    LOG.debug("AutoRank Cache cleanup has begun");
    // ID / Main cache
    List<String> toBeRemoved = new ArrayList<>();
    for (CacheAutoRank entry : autoRankCache.values()) {
      if (needsUpdate(entry)) {
        toBeRemoved.add(entry.autoRank.rank);
      }
    }
    // Remove from Cache
    int count = 0;
    for (String entry : toBeRemoved) {
      count++;
      invalidate(entry.toUpperCase());
    }
    LOG.debug("AutoRank Cache has been cleaned, " + count + " entries has been removed!");
  }

  /** This should do nothing, its here to prevent an possible reflection issue */
  public static void cleanupDB() {
  }

  /** Get the table columns beside the ID */
  public static String[] getColumns() {
    return Arrays.copyOfRange(AUTORANKS_COLUMNS, 1, AUTORANKS_COLUMNS.length);
  }
}
