/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheTransferUUID implements Cache {

  public String[] transferCacheEntrys;
  public long lastSync;

  public CacheTransferUUID(String[] transferCacheEntrys, long lastSync) {
    this.transferCacheEntrys = transferCacheEntrys;
    this.lastSync = lastSync;
  }

  public CacheTransferUUID(String[] transferCacheEntrys) {
    this.transferCacheEntrys = transferCacheEntrys;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return lastSync;
  }
}
