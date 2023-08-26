/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.TransferEntry;

public class CacheTransfer implements Cache {

  public TransferEntry transferEntry;
  public long lastSync;

  public CacheTransfer(TransferEntry transferEntry, long lastSync) {
    this.transferEntry = transferEntry;
    this.lastSync = lastSync;
  }

  public CacheTransfer(TransferEntry transferEntry) {
    this.transferEntry = transferEntry;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
