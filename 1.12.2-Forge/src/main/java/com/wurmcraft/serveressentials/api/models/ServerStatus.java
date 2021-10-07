package com.wurmcraft.serveressentials.api.models;

public class ServerStatus {

    public String serverID;
    public Long delayMS;
    public Long lastUpdate;
    public String[] onlinePlayers;
    public String[] playerInfo;
    public String currentState;
    public String specialData;

    public ServerStatus(String serverID, Long delayMS, Long lastUpdate, String[] onlinePlayers, String[] playerInfo, String currentState, String specialData) {
        this.serverID = serverID;
        this.delayMS = delayMS;
        this.lastUpdate = lastUpdate;
        this.onlinePlayers = onlinePlayers;
        this.playerInfo = playerInfo;
        this.currentState = currentState;
        this.specialData = specialData;
    }
}
