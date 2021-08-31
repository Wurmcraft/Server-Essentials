package com.wurmcraft.serveressentials.api.models;

import java.util.Objects;

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
    public TrackedStat(
            String serverID, String uuid, long timestamp, String eventType, String eventData) {
        this.serverID = serverID;
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.eventType = eventType;
        this.eventData = eventData;
    }

    public TrackedStat() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackedStat)) return false;
        TrackedStat that = (TrackedStat) o;
        return timestamp == that.timestamp
                && Objects.equals(serverID, that.serverID)
                && Objects.equals(uuid, that.uuid)
                && Objects.equals(eventType, that.eventType)
                && Objects.equals(eventData, that.eventData);
    }
}
