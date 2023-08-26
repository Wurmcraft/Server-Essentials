/**
 * This file is part of Server Essentials, licensed under the GNU General Public License
 * v3.0.
 *
 * <p>Copyright (c) 2022 Wurmcraft
 */
package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.Currency;

public class CacheCurrency implements Cache {

  public Currency currency;
  public long lastSync;

  public CacheCurrency(Currency currency, long lastSync) {
    this.currency = currency;
    this.lastSync = lastSync;
  }

  public CacheCurrency(Currency currency) {
    this.currency = currency;
    this.lastSync = System.currentTimeMillis();
  }

  @Override
  public long lastSync() {
    return 0;
  }
}
