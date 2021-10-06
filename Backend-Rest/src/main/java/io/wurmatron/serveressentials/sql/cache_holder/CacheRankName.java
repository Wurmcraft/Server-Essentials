package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheRankName implements Cache {

    public String name;
    public long lastSync;


    public CacheRankName(String name,long lastSync) {
        this.name = name;
        this.lastSync = lastSync;
    }

    public CacheRankName(String name) {
        this.name = name;
        this.lastSync = System.currentTimeMillis();
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
