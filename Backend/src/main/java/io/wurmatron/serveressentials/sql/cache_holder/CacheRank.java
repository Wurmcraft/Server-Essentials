/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.Rank;

public class CacheRank implements Cache {

  public Rank rank;
  public long lastSync;

  public CacheRank(Rank rank, long lastSync) {
    this.rank = rank;
    this.lastSync = lastSync;
  }

  public CacheRank(Rank rank) {
    this.rank = rank;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
