/**
 * This file is part of Server Essentials, licensed under the GNU General Public License v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.Ban;

public class CacheBan implements Cache {

  public Ban ban;
  public long lastSync;

  public CacheBan(Ban ban, long lastSync) {
    this.ban = ban;
    this.lastSync = lastSync;
  }

  public CacheBan(Ban ban) {
    this.ban = ban;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
