package io.wurmatron.serveressentials.models;

public class TrackedStat {

    public String serverID;
    public String uuid;
    public long timestamp;
    public String eventType;
    public String eventData;

    /**
     * @param serverID  id of the server where this occurred
     * @param uuid      uuid of the person for the event
     * @param timestamp unix timestamp for this event
     * @param eventType type of event that has occurred
     * @param eventData data related to the event
     */
    public TrackedStat(String serverID, String uuid, long timestamp, String eventType, String eventData) {
        this.serverID = serverID;
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.eventData = eventData;
    }
}
