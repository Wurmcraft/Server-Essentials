package com.wurmcraft.serveressentials.forge.api.json.player;

/**
 * Keeps track of the playtime on the network
 */
public class NetworkTime {

  public ServerTime[] serverTime;

  public NetworkTime() {
    this.serverTime = new ServerTime[0];
  }

  public NetworkTime(ServerTime[] serverTime) {
    this.serverTime = serverTime;
  }

  public static class ServerTime {

    public String serverID;
    public long time;

    @Deprecated
    public ServerTime() {
      this.serverID = "";
      this.time = 0;
    }

    /**
     * @param serverID ID of the server, (Set in the config)
     * @param time Amount of time
     */
    public ServerTime(String serverID, long time) {
      this.serverID = serverID;
      this.time = time;
    }
  }

}
