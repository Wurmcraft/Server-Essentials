package io.wurmatron.serveressentials.sql.cache_holder;

import io.wurmatron.serveressentials.models.Account;

public class CacheAccount implements Cache {

    public Account account;
    public long lastSync;

    public CacheAccount(Account account, long lastSync) {
        this.account = account;
        this.lastSync = lastSync;
    }

    public CacheAccount(Account account) {
        this.account = account;
        this.lastSync = System.currentTimeMillis();
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
