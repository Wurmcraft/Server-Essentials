package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.models.transfer.ItemWrapper;

import java.util.Arrays;

public class TransferEntry {

    public Long transferID;
    public String uuid;
    public Long startTime;
    public ItemWrapper[] items;
    public String serverID;

    /**
     * @param transferID id of the given transfer (Don't change as this is used internally to track)
     * @param uuid       uuid of the person that started the item transfer
     * @param startTime  time the transfer was started, (in case of timeouts)
     * @param items      list of items to be transferred (in json format)
     * @param serverID   id of the server that the transfer was started on
     */
    public TransferEntry(
            long transferID, String uuid, long startTime, ItemWrapper[] items, String serverID) {
        this.transferID = transferID;
        this.uuid = uuid;
        this.startTime = startTime;
        this.items = items;
        this.serverID = serverID;
    }

    public TransferEntry() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransferEntry)) return false;
        TransferEntry that = (TransferEntry) o;
        return transferID.equals(that.transferID)
                && startTime.equals(that.startTime)
                && uuid.equals(that.uuid)
                && Arrays.equals(that.items, items)
                && serverID.equals(that.serverID);
    }
}
