package io.wurmatron.serveressentials.sql.cacheHolder;

import io.wurmatron.serveressentials.models.Donator;

public class CacheDonator implements Cache {

    public Donator donator;
    public long lastSync;

    public CacheDonator(Donator donator, long lastSync) {
        this.donator = donator;
        this.lastSync = lastSync;
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
