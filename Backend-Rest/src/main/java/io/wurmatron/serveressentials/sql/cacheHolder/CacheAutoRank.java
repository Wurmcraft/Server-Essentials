package io.wurmatron.serveressentials.sql.cacheHolder;

import io.wurmatron.serveressentials.models.AutoRank;

public class CacheAutoRank implements Cache {

    public AutoRank autoRank;
    public long lastSync;

    public CacheAutoRank(AutoRank autoRank, long lastSync) {
        this.autoRank = autoRank;
        this.lastSync = lastSync;
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
