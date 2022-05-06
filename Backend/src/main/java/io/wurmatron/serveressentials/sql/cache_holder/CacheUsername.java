package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheUsername implements Cache {

    public String username;
    public long lastSync;

    public CacheUsername(String username, long lastSync) {
        this.username = username;
        this.lastSync = lastSync;
    }

    public CacheUsername(String username) {
        this.username = username;
        this.lastSync = System.currentTimeMillis();
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
