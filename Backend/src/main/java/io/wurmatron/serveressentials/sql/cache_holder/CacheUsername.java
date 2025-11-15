/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheUsername implements Cache {

  public String username;
  public long lastSync;

  public CacheUsername(String username, long lastSync) {
    this.username = username;
    this.lastSync = lastSync;
  }

  public CacheUsername(String username) {
    this.username = username;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
