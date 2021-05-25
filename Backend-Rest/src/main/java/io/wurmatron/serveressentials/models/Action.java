package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

import java.util.Objects;

public class Action {

    public String relatedID;
    public String host;
    public String action;
    public String actionData;
    public Long timestamp;

    /**
     * @param relatedID  serverID/discord Channel ID
     * @param host       "Minecraft", "Discord"
     * @param action     Name of the given action, that has happened
     * @param actionData Json data related to the given action
     * @param timestamp  Unix Timestamp for when the action occurred
     */
    public Action(String relatedID, String host, String action, String actionData, long timestamp) {
        this.relatedID = relatedID;
        this.host = host;
        this.action = action;
        this.actionData = actionData;
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
        Action action1 = (Action) o;
        return Objects.equals(relatedID, action1.relatedID) && Objects.equals(host, action1.host) && Objects.equals(action, action1.action) && Objects.equals(actionData, action1.actionData) && Objects.equals(timestamp, action1.timestamp);
    }

    @Override
    public Rank clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Rank.class);
    }
}
