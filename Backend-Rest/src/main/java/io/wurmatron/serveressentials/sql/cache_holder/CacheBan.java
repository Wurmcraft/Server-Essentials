package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.Ban;

public class CacheBan implements Cache {

    public Ban ban;
    public long lastSync;

    public CacheBan(Ban ban, long lastSync) {
        this.ban = ban;
        this.lastSync = lastSync;
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
