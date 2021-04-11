package io.wurmatron.serveressentials.sql.cacheHolder;

import io.wurmatron.serveressentials.models.Currency;

public class CacheCurrency implements Cache{

    public Currency currency;
    public long lastSync;

    public CacheCurrency(Currency currency, long lastSync) {
        this.currency = currency;
        this.lastSync = lastSync;
    }

    @Override
    public long lastSync() {
        return 0;
    }
}
