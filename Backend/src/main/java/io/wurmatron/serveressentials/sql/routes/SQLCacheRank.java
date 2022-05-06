/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.*;

import io.wurmatron.serveressentials.models.Rank;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.cache_holder.CacheRank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public class SQLCacheRank extends SQLCache {

  public static final String RANKS_TABLE = "ranks";

  private static long lastFullSync = System.currentTimeMillis();

  /**
   * Get a rank based on its id
   *
   * @param name Name for the given rank
   * @return instance of the rank
   * @see io.wurmatron.serveressentials.sql.SQLGenerator#get(String, String, String, String, Object)
   * @see #invalidate(String)
   */
  @Nullable
  public static Rank get(String name) {
    // Attempt to get from cache
    if (rankCache.contains(name))
      if (!needsUpdate(rankCache.get(name))) return rankCache.get(name).rank;
      else rankCache.remove(name);
    // Not in cache / invalid
    try {
      Rank rank = get("*", RANKS_TABLE, "name", "" + name, new Rank());
      if (rank != null) {
        rankCache.put(name.toUpperCase(), new CacheRank(rank));
        return rank;
      }
    } catch (Exception e) {
      LOG.debug("Failed to find rank with name '" + name + "'(" + e.getMessage() + ")");
    }
    // Rank does not exist
    return null;
  }

  /**
   * Gets a list of all the ranks
   *
   * @return a list of all the ranks
   * @see io.wurmatron.serveressentials.sql.SQLGenerator#getArray(String, String, String, String,
   *     Object)
   */
  public static List<Rank> get() {
    // Attempt to get from cache
    if (lastFullSync + (config.server.cacheTime * 1000L) > System.currentTimeMillis()) {
      List<Rank> ranks = new ArrayList<>();
      for (CacheRank cache : rankCache.values())
        if (!needsUpdate(cache)) ranks.add(cache.rank);
        else { // Update entry
          invalidate(cache.rank.name);
          Rank rank = get(cache.rank.name);
          if (rank != null) ranks.add(rank);
        }
    }
    // Full Sync
    try {
      List<Rank> ranks = getArray("*", RANKS_TABLE, "", "", new Rank());
      if (ranks.size() > 0) {
        // Add to cache
        for (Rank rank : ranks) rankCache.put(rank.name.toUpperCase(), new CacheRank(rank));
        lastFullSync = System.currentTimeMillis();
        return ranks;
      }
    } catch (Exception e) {
      LOG.debug("Failed to get ranks (" + e.getMessage() + ")");
    }
    return new ArrayList<>();
  }

  /**
   * Creates a new rank
   *
   * @param rank instance to be created
   * @return instance that was created, with id
   * @see io.wurmatron.serveressentials.sql.SQLGenerator#insert(String, String[], Object, boolean)
   */
  public static Rank create(Rank rank) {
    try {
      rank.name = rank.name.toLowerCase();
      insert(RANKS_TABLE, Arrays.copyOfRange(RANKS_COLUMNS, 1, RANKS_COLUMNS.length), rank, false);
      rankCache.put(rank.name, new CacheRank(rank));
      return rank;
    } catch (Exception e) {
      LOG.debug("Failed to add rank with name '" + rank.name + "'(" + e.getMessage() + ")");
      LOG.debug("Rank: " + GSON.toJson(rank));
    }
    return null;
  }

  /**
   * Updates a existing rank
   *
   * @param rank updated rank instance
   * @return if the rank has been updated or not
   * @see io.wurmatron.serveressentials.sql.SQLGenerator#update(String, String[], String, String,
   *     Object)
   */
  public static boolean update(Rank rank, String[] columnsToUpdate) {
    try {
      update(RANKS_TABLE, columnsToUpdate, "name", rank.name + "", rank);
      if (rankCache.contains(rank.name)) {
        rankCache.get(rank.name).rank =
            updateInfoLocal(columnsToUpdate, rank, rankCache.get(rank.name).rank);
        rankCache.get(rank.name).lastSync = System.currentTimeMillis();
      } else { // Missing from cache
        rankCache.put(rank.name.toUpperCase(), new CacheRank(rank));
      }
      return true;
    } catch (Exception e) {
      LOG.debug("Failed to update rank with id '" + rank.name + "'(" + e.getMessage() + ")");
      LOG.debug("Rank: " + GSON.toJson(rank));
    }
    return false;
  }

  /**
   * Deletes the given rank
   *
   * @param name Name of the rank to be deleted
   * @return if the rank has been deleted or not
   * @see io.wurmatron.serveressentials.sql.SQLGenerator#delete(String, String, String)
   * @see #invalidate(String)
   */
  public static boolean delete(String name) {
    try {
      delete(RANKS_TABLE, "name", name + "");
      invalidate(name);
      return true;
    } catch (Exception e) {
      LOG.debug("Failed to delete rank with id '" + name + "' (" + e.getMessage() + ")");
    }
    return false;
  }

  /** @param name Name of the rank to remove from the cache */
  public static void invalidate(String name) {
    rankCache.remove(name);
    LOG.debug("Rank '" + name + " has been invalidated, will be updated on next request!");
  }

  /** Cleanup the stored cache and look for expired entries */
  public static void cleanupCache() {
    LOG.info("Rank Cache cleanup has begun!");
    // ID Cache
    List<String> toBeRemoved = new ArrayList<>();
    for (CacheRank rank : rankCache.values())
      if (needsUpdate(rank)) toBeRemoved.add(rank.rank.name);
    // Remove from Cache
    int count = 0;
    for (String name : toBeRemoved) {
      count++;
      invalidate(name);
    }
    LOG.info("RankCache has been cleaned, " + count + " entries have been removed!");
  }

  /** Removes the expired entries from the database */
  public static void cleanupDB() {}

  /**
   * List all the table columns besides the key, in this case 'rankID'
   *
   * @return List of all the columns in the table, except the key
   */
  public static String[] getColumns() {
    return Arrays.copyOfRange(RANKS_COLUMNS, 1, RANKS_COLUMNS.length);
  }
}
