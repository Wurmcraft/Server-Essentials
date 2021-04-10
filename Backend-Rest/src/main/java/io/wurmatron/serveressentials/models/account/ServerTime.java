package io.wurmatron.serveressentials.models.account;

public class ServerTime {
    public String serverID;
    public long totalTime;
    public long lastSeen;

    /**
     * @param serverID  serverID that this related to
     * @param totalTime total time in minutes
     * @param lastSeen  unix timestmap, for when the user was last seen on this id
     */
    public ServerTime(String serverID, long totalTime, long lastSeen) {
        this.serverID = serverID;
        this.totalTime = totalTime;
        this.lastSeen = lastSeen;
    }
}
