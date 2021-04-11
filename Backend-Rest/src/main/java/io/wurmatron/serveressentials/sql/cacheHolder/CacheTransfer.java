package io.wurmatron.serveressentials.sql.cacheHolder;

import io.wurmatron.serveressentials.models.TransferEntry;

public class CacheTransfer implements Cache {

    public TransferEntry transferEntry;
    public long lastSync;

    public CacheTransfer(TransferEntry transferEntry, long lastSync) {
        this.transferEntry = transferEntry;
        this.lastSync = lastSync;
    }

    @Override
    public long lastSync() {
        return lastSync;
    }
}
