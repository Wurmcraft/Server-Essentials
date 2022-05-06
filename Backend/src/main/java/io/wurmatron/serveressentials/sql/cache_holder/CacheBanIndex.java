package io.wurmatron.serveressentials.sql.cache_holder;

public class CacheBanIndex implements Cache {

    public int[] banIDs;
    public long lastSync;

    public CacheBanIndex(int[] banIDs, long lastSync) {
        this.banIDs = banIDs;
        this.lastSync = lastSync;
    }

    public CacheBanIndex(int[] banIDs) {
        this.banIDs = banIDs;
        this.lastSync = System.currentTimeMillis();
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
