/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql;

import static io.wurmatron.serveressentials.ServerEssentialsRest.config;

import io.wurmatron.serveressentials.sql.cache_holder.*;
import java.util.*;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

/**
 * Between direct sql and the caching mechanism
 *
 * @see SQLGenerator Direct SQL
 */
public class SQLCache extends SQLGenerator {

  // Cache (applicably only)
  protected static final NonBlockingHashMap<String, CacheAutoRank> autoRankCache =
      new NonBlockingHashMap<>();
  protected static final NonBlockingHashMap<Long, CacheBan> bansCache = new NonBlockingHashMap<>();
  protected static final NonBlockingHashMap<String, CacheCurrency> currencyCache =
      new NonBlockingHashMap<>();
  protected static final NonBlockingHashMap<String, CacheDonator> donatorCache =
      new NonBlockingHashMap<>();
  protected static final NonBlockingHashMap<String, List<CacheMarket>> marketCache =
      new NonBlockingHashMap<>();
  protected static final NonBlockingHashMap<String, CacheRank> rankCache =
      new NonBlockingHashMap<>();
  protected static final NonBlockingHashMap<Long, CacheTransfer> transferCache =
      new NonBlockingHashMap<Long, CacheTransfer>();
  protected static final NonBlockingHashMap<String, CacheAccount> accountCache =
      new NonBlockingHashMap<>();

  /**
   * Checks if a cached entry has expired or not
   *
   * @param cacheData entry from the cache
   * @return if a cache entry has expired and needs updating
   */
  protected static <T extends Cache> boolean needsUpdate(T cacheData) {
    return cacheData.lastSync() + (config.server.cacheTime * 1000L) < System.currentTimeMillis();
  }
}
