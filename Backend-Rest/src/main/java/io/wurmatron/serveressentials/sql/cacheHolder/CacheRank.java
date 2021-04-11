package io.wurmatron.serveressentials.sql.cacheHolder;

import io.wurmatron.serveressentials.models.Rank;

public class CacheRank implements Cache{

    public Rank rank;
    public long lastSync;

    public CacheRank(Rank rank, long lastSync) {
        this.rank = rank;
        this.lastSync = lastSync;
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
