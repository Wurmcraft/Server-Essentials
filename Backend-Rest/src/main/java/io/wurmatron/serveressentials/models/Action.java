package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

public class Action {

    public String related_id;
    public String host;
    public String action;
    public String action_data;
    public Long timestamp;

    /**
     * @param relatedID  serverID/discord Channel ID
     * @param host       "Minecraft", "Discord"
     * @param action     Name of the given action, that has happened
     * @param actionData Json data related to the given action
     * @param timestamp  Unix Timestamp for when the action occurred
     */
    public Action(String relatedID, String host, String action, String actionData, long timestamp) {
        this.related_id = relatedID;
        this.host = host;
        this.action = action;
        this.action_data = actionData;
        this.timestamp = timestamp;
    }

    public Action() {
    }

    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Action)) return false;
        Action other = (Action) o;
        return related_id.equals(other.related_id) && host.equals(other.host) && action.equals(other.action) && timestamp.equals(other.timestamp);
    }

    @Override
    public Action clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Action.class);
    }
}
