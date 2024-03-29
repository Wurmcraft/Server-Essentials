/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.MarketEntry;

public class CacheMarket implements Cache {

  public MarketEntry marketEntry;
  public long lastSync;

  public CacheMarket(MarketEntry marketEntry, long lastSync) {
    this.marketEntry = marketEntry;
    this.lastSync = lastSync;
  }

  public CacheMarket(MarketEntry marketEntry) {
    this.marketEntry = marketEntry;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
