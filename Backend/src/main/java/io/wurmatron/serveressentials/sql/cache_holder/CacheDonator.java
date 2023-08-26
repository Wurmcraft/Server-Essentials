/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.Donator;

public class CacheDonator implements Cache {

  public Donator donator;
  public long lastSync;

  public CacheDonator(Donator donator, long lastSync) {
    this.donator = donator;
    this.lastSync = lastSync;
  }

  public CacheDonator(Donator donator) {
    this.donator = donator;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
