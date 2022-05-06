/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheRankName implements Cache {

  public String name;
  public long lastSync;

  public CacheRankName(String name, long lastSync) {
    this.name = name;
    this.lastSync = lastSync;
  }

  public CacheRankName(String name) {
    this.name = name;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
