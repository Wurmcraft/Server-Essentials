package com.wurmcraft.serveressentials.api.models;

import java.util.Objects;

public class LogEntry {

    public String serverID;
    public Long timestamp;
    public String actionType;
    public String actionData;
    public String uuid;
    public Integer x;
    public Integer y;
    public Integer z;
    public Integer dim;

    /**
     * @param serverID   id of the server this occurred
     * @param timestamp  unix timestamp for when this action occurred
     * @param actionType name / type of action that has occurred
     * @param actionData data related to the action
     * @param uuid       uuid of the user that this is related to
     * @param x          x pos that this occurred (if applicable)
     * @param y          y pos that this occurred (if applicable)
     * @param z          z pos that this occurred (if applicable)
     * @param dim        dimension that this occurred (if applicable)
     */
    public LogEntry(
            String serverID,
            long timestamp,
            String actionType,
            String actionData,
            String uuid,
            int x,
            int y,
            int z,
            int dim) {
        this.serverID = serverID;
        this.timestamp = timestamp;
        this.actionType = actionType;
        this.actionData = actionData;
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public LogEntry() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogEntry)) return false;
        LogEntry logEntry = (LogEntry) o;
        return timestamp.equals(logEntry.timestamp)
                && x.equals(logEntry.x)
                && y.equals(logEntry.y)
                && z.equals(logEntry.z)
                && dim.equals(logEntry.dim)
                && Objects.equals(serverID, logEntry.serverID)
                && Objects.equals(actionType, logEntry.actionType)
                && Objects.equals(actionData, logEntry.actionData)
                && Objects.equals(uuid, logEntry.uuid);
    }
}
