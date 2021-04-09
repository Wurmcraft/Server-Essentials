package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;

public class Action {

    public String relatedID;
    public String host;
    public String action;
    public String actionData;
    public long timestamp;

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

    @Override
    public String toString() {
        return ServerEssentialsRest.GSON.toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Action) {
            Action otherAction = (Action) obj;
            return relatedID.equalsIgnoreCase(otherAction.relatedID) && host.equalsIgnoreCase(otherAction.host) && action.equalsIgnoreCase(otherAction.action);
        }
        return false;
    }
}
