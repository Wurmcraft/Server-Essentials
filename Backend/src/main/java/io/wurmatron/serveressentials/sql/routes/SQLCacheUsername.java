/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.routes;

import static io.wurmatron.serveressentials.ServerEssentialsRest.LOG;
import static io.wurmatron.serveressentials.sql.routes.SQLCacheAccount.USERS_TABLE;

import io.wurmatron.serveressentials.models.Account;
import io.wurmatron.serveressentials.sql.SQLCache;
import io.wurmatron.serveressentials.sql.SQLGenerator;
import io.wurmatron.serveressentials.sql.cache_holder.CacheUsername;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class SQLCacheUsername extends SQLCache {

  private static final NonBlockingHashMap<String, CacheUsername> usernameCache =
      new NonBlockingHashMap<>();
  private static final NonBlockingHashMap<String, String> uuidCache = new NonBlockingHashMap<>();

  /**
   * Gets the user's username via uuid data from the cache, if not requests from database Note: If
   * the data accuracy is important, consider invalidating the uuid before calling this, this will
   * force an update
   *
   * @param uuid uuid of the username to lookup
   * @return username, based on the uuid
   * @see SQLGenerator#get(String, String, String, String, Object)
   * @see #invalidate(String)
   */
  @Nullable
  public static String getUsername(String uuid) {
    // Attempt to get from cache
    if (usernameCache.containsKey(uuid))
      if (!needsUpdate(usernameCache.get(uuid))) return usernameCache.get(uuid).username;
      else invalidate(uuid);
    // Not in cache / invalid
    try {
      Account account = get("*", USERS_TABLE, "uuid", uuid, new Account());
      if (account != null) {
        usernameCache.put(uuid, new CacheUsername(account.username));
        uuidCache.put(account.username, uuid);
        return account.username;
      }
    } catch (Exception e) {
      LOG.debug(
          "Failed to find account username with uuid '" + uuid + "' (" + e.getMessage() + ")");
    }
    // uuid does not exist
    return null;
  }

  /**
   * Gets the user's uuid via username data from the cache, if not requests from database Note: If
   * the data accuracy is important, consider invalidating the uuid before calling this, this will
   * force an update
   *
   * @param username username of the uuid to lookup
   * @return uuid, based on the username
   * @see SQLGenerator#get(String, String, String, String, Object)
   * @see #invalidate(String)
   */
  @Nullable
  public static String getUUID(String username) {
    // Attempt to get from cache
    if (uuidCache.containsKey(username))
      if (!needsUpdate(usernameCache.get(uuidCache.get(username)))) return uuidCache.get(username);
      else invalidate(uuidCache.get(username));
    // Not in cache / invalid
    try {
      Account account = get("*", USERS_TABLE, "username", username, new Account());
      if (account != null) {
        usernameCache.put(account.uuid, new CacheUsername(account.username));
        uuidCache.put(username, account.uuid);
        return account.uuid;
      }
    } catch (Exception e) {
      LOG.debug("Failed to find account with username '" + username + "' (" + e.getMessage() + ")");
    }
    // Username does not exist
    return null;
  }

  /**
   * Removes a entry from the cache, causing an update upon next request Note: Does not delete
   * anything from the database
   *
   * @param uuid uuid of the username to remove from the cache
   */
  public static void invalidate(String uuid) {
    usernameCache.remove(uuid);
    for (Iterator<String> it = uuidCache.keySet().iterator(); it.hasNext(); ) {
      String username = it.next();
      if (uuidCache.get(username).equalsIgnoreCase(uuid)) uuidCache.remove(username);
    }
    LOG.debug("Username Entry '" + uuid + " has been invalidated, will update on next request!");
  }

  /** Cleanup the stored cache and look for expired entries */
  public static void cleanupCache() {
    LOG.info("Username Cache cleanup has begun!");
    List<String> toBeRemoved = new ArrayList<>();
    for (String uuid : usernameCache.keySet())
      if (needsUpdate(usernameCache.get(uuid))) toBeRemoved.add(uuid);
    // Remove from Cache
    int count = 0;
    for (String uuid : toBeRemoved) SQLCacheAccount.invalidate(uuid);
    count++;
    LOG.info("Username Cache has been cleaned, " + count + " entries have been removed!");
  }

  /** Removes the expired entries from the database */
  public static void cleanupDB() {}
}
