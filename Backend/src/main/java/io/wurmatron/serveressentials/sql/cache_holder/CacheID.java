/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheID implements Cache {

  public long id;
  public long lastSync;

  public CacheID(int id, long lastSync) {
    this.id = id;
    this.lastSync = lastSync;
  }

  public CacheID(long id) {
    this.id = id;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
