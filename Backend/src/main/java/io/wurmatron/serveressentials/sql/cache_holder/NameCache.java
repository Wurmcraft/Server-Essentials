/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

public class NameCache implements Cache {

  public long id;
  public long lastSync;

  public NameCache(long id, long lastSync) {
    this.id = id;
    this.lastSync = lastSync;
  }

  public NameCache(long id) {
    this.id = id;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
