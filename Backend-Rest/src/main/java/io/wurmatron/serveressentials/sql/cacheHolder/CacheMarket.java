package io.wurmatron.serveressentials.sql.cacheHolder;

import io.wurmatron.serveressentials.models.MarketEntry;

public class CacheMarket implements Cache{

    public MarketEntry marketEntry;
    public long lastSync;

    public CacheMarket(MarketEntry marketEntry, long lastSync) {
        this.marketEntry = marketEntry;
        this.lastSync = lastSync;
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
