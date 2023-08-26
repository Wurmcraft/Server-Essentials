/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.GSON;
import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;

import io.wurmatron.serveressentials.models.Ban;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheBan;
import io.wurmatron.serveressentials.sql.cache_holder.CacheBanIndex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class SQLCacheBan extends SQLCache {

  public static String BAN_TABLE = "bans";

  public static NonBlockingHashMap<String, CacheBanIndex> uuidCache = new NonBlockingHashMap<>();

  /**
   * Returns the given instance based on the banID, if one exists
   *
   * @param banID id of the ban
   * @return instance of the ban, from the db, if exists
   * @see SQLGenerator#get(String, String, String, String, Object)
   */
  @Nullable
  public static Ban get(long banID) {
    // Attempt to get from cache
    if (bansCache.containsKey(banID)) {
      if (!needsUpdate(bansCache.get(banID))) {
        return bansCache.get(banID).ban;
      } else {
        invalidate(banID);
      }
    }
    // Not in cache / invalid
    try {
      Ban ban = get("*", BAN_TABLE, "ban_id", "" + banID, new Ban());
      if (ban != null) {
        bansCache.put(banID, new CacheBan(ban));
        return ban.clone();
      }
    } catch (Exception e) {
      LOG.debug("Failed to find ban with id '" + banID + "' (" + e.getMessage() + ")");
    }
    return null;
  }

  /**
   * Returns the given instance based on the user uuid, if one exists
   *
   * @param uuid uuid of the banned user
   * @return instance of the ban, from the db, if exists
   * @see SQLGenerator#getArray(String, String, String, String, Object)
   */
  public static Ban[] get(String uuid) {
    // Attempt to get from cache
    if (uuidCache.containsKey(uuid)) {
      if (!needsUpdate(uuidCache.get(uuid))) {
        int[] cachedEntries = uuidCache.get(uuid).banIDs;
        List<Ban> bans = new ArrayList<>();
        for (int id : cachedEntries) {
          bans.add(get(id));
        }
        return bans.toArray(new Ban[0]);
      } else {
        invalidate(uuid);
      }
    }
    // Not in cache / invalid
    try {
      List<Ban> bans = getArray("*", BAN_TABLE, "uuid", uuid, new Ban());
      List<Integer> banIDs = new ArrayList<>();
      for (Ban ban : bans) {
        bansCache.put(ban.ban_id, new CacheBan(ban));
        banIDs.add((int) ban.ban_id);
      }
      uuidCache.put(uuid, new CacheBanIndex(banIDs.stream().mapToInt(x -> x).toArray()));
      return bans.toArray(new Ban[0]);
    } catch (Exception e) {
      LOG.debug("Failed to lookup bans for '" + uuid + "' (" + e.getMessage() + ")");
    }
    // Not in cache / invalid
    return new Ban[0];
  }

  /**
   * Returns a list of all the ban instances within the db
   *
   * @return instance of all the bans in the db, if any exists
   * @see SQLGenerator#getArray(String, String, String, String, Object)
   */
  public static List<Ban> get() {
    int count = 0;
    List<Ban> bans = new ArrayList<>();
    try {
      bans = getAll("*", BAN_TABLE, new Ban());
      count = bans.size();
    } catch (Exception e) {
      LOG.debug("Failed to count bans (" + e.getMessage() + ")");
    }
    if (count > 0) {
      // Check if cache is complete
      if (bansCache.size() >= count) {
        for (CacheBan ban : bansCache.values()) {
          bans.add(get(ban.ban.ban_id));
        }
        return bans;
      } else {
        try {
          bansCache.clear();
          for (Ban ban : bans) {
            bansCache.put(ban.ban_id, new CacheBan(ban));
          }
          return bans;
        } catch (Exception e) {
          LOG.debug("Failed to fetch Ban's (" + e.getMessage() + ")");
        }
      }
    }
    return new ArrayList<>();
  }

  /**
   * Creates a new ban in the db
   *
   * @param ban instance of the ban to be created
   * @return ban instance with all the data filled out, in this case 'banID' is returned
   * @see SQLGenerator#insert(String, String[], Object, boolean)
   */
  @Nullable
  public static Ban create(Ban ban) {
    try {
      String[] columns = getColumns();
      // Check if discordID exists, if not remove
      if (ban.discord_id.isEmpty()) {
        List<String> columnList = new ArrayList<>();
        for (String column : columns) {
          if (!column.equalsIgnoreCase("discord_id")) {
            columnList.add(column);
          }
        }
        columns = columnList.toArray(new String[0]);
      }
      ban.ban_id = insert(BAN_TABLE, columns, ban, true);
      bansCache.put(ban.ban_id, new CacheBan(ban));
      return ban;
    } catch (Exception e) {
      LOG.debug("Failed to create new ban for uuid '" + ban.uuid + "' (" + e.getMessage()
          + ")");
      LOG.debug("Ban: " + GSON.toJson(ban));
    }
    return null;
  }

  /**
   * Updates a ban entry in the db
   *
   * @param ban instance of the ban to pull the updates from
   * @param columnsToUpdate list of the variables that need to be updated
   * @return if the update was successful
   * @see SQLGenerator#update(String, String[], String, String, Object)
   */
  public static boolean update(Ban ban, String[] columnsToUpdate) {
    try {
      update(BAN_TABLE, columnsToUpdate, "ban_id", "" + ban.ban_id, ban);
      invalidate(ban.ban_id);
      return true;
    } catch (Exception e) {
      LOG.debug(
          "Failed to update ban for uuid '" + ban.uuid + "' (" + e.getMessage() + ")");
      LOG.debug("BAN: " + GSON.toJson(ban));
    }
    return false;
  }

  /**
   * Delete / Remove a ban from the db
   *
   * @param banID id of the ban to be deleted
   * @return if the ban was deleted successfully
   */
  public static boolean delete(long banID) {
    try {
      delete(BAN_TABLE, "ban_id", "" + banID);
      invalidate(banID);
      return true;
    } catch (Exception e) {
      LOG.debug("Ban entry with ID '" + banID + "' failed to be deleted");
    }
    return false;
  }

  /**
   * Invalidates / removes the given ban entry
   *
   * @param banID id if the ban to remove from the sql cache
   */
  public static void invalidate(long banID) {
    bansCache.remove(banID);
    LOG.debug("Ban '" + banID + "' has been invalidated, will update on next request");
  }

  /**
   * Invalidates / removes the given user's ban entry
   *
   * @param uuid uuid of the user to remove from the sql cache
   */
  public static void invalidate(String uuid) {
    uuidCache.remove(uuid);
    LOG.debug(
        "Ban's for '" + uuid + "' has been invalidated, will update on next request");
  }

  /** Run periodically to cleanup the cache and remove expired / invalid entries */
  public static void cleanupCache() {
    LOG.debug("Ban Cache cleanup has begun!");
    // ID Cache
    List<Long> toToRemoved = new ArrayList<>();
    for (CacheBan ban : bansCache.values()) {
      if (needsUpdate(ban)) {
        toToRemoved.add(ban.ban.ban_id);
      }
    }
    // UUID Cache
    List<String> toBeRemovedUUID = new ArrayList<>();
    for (String uuid : uuidCache.keySet()) {
      if (needsUpdate(uuidCache.get(uuid))) {
        toBeRemovedUUID.add(uuid);
      }
    }
    // Remove from Cache
    int count = 0;
    for (long id : toToRemoved) {
      count++;
      invalidate(id);
    }
    for (String uuid : toBeRemovedUUID) {
      count++;
      invalidate(uuid);
    }
    LOG.debug("Ban Cache has been cleaned, " + count + " entries have been removed!");
  }

  /** Run periodically to cleanup the db and remove expired / invalid entries */
  public static void cleanupDB() {
    LOG.debug("Checking for bans to timeout / update!");
    try {
      List<Ban> bans = getArray("*", BAN_TABLE, "", "", new Ban());
      int count = 0;
      for (Ban ban : bans) {
        if (ban.ban_status && handleBanUpdateCheck(ban)) {
          count++;
        }
      }
      LOG.debug(count + " bans have been updated!");
    } catch (Exception e) {
      LOG.debug("Failed to fetch bans (" + e.getMessage() + ")");
    }
  }

  /**
   * Checks if the provided ban needs to be updated / unbanned based on the current time
   *
   * @param ban instance of the ban to check for updates
   * @return if the ban has been updated or not
   */
  // TODO Implement
  private static boolean handleBanUpdateCheck(Ban ban) {

    return false;
  }

  /** Get all the columns except the id */
  public static String[] getColumns() {
    return Arrays.copyOfRange(BANS_COLUMNS, 1, BANS_COLUMNS.length);
  }
}
