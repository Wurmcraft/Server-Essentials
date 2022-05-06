package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.util.Objects;

public class LogEntry {

    public String server_id;
    public Long timestamp;
    public String action_type;
    public String action_data;
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
     * @param dim       dimension that this occurred (if applicable)
     */
    public LogEntry(String serverID, long timestamp, String actionType, String actionData, String uuid, int x, int y, int z, int dim) {
        this.server_id = serverID;
        this.timestamp = timestamp;
        this.action_type = actionType;
        this.action_data = actionData;
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
        return timestamp.equals(logEntry.timestamp) && x.equals(logEntry.x) && y.equals(logEntry.y) && z.equals(logEntry.z) && dim.equals(logEntry.dim) && Objects.equals(server_id, logEntry.server_id) && Objects.equals(action_type, logEntry.action_type) && Objects.equals(action_data, logEntry.action_data) && Objects.equals(uuid, logEntry.uuid);
    }

    @Override
    public LogEntry clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, LogEntry.class);
    }
}
