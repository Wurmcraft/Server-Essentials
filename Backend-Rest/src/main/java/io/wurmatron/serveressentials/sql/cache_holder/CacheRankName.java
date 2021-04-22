package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheRankName implements Cache {

    public long rankID;
    public long lastSync;


    public CacheRankName(long rankID,long lastSync) {
        this.rankID = rankID;
        this.lastSync = lastSync;
    }

    public CacheRankName(long rankID) {
        this.rankID = rankID;
        this.lastSync = System.currentTimeMillis();
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
