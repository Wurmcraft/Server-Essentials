package io.wurmatron.serveressentials.models;

public class TransferEntry {

    public long transferID;
    public String uuid;
    public long startTime;
    public String[] items;
    public String serverID;

    /**
     * @param transferID id of the given transfer (Don't change as this is used internally to track)
     * @param uuid       uuid of the person that started the item transfer
     * @param startTime  time the transfer was started, (in case of timeouts)
     * @param items      list of items to be transferred (in json format)
     * @param serverID   id of the server that the transfer was started on
     */
    public TransferEntry(long transferID, String uuid, long startTime, String[] items, String serverID) {
        this.transferID = transferID;
        this.uuid = uuid;
        this.startTime = startTime;
        this.items = items;
        this.serverID = serverID;
    }

    public TransferEntry() {
    }
}
