package com.wurmcraft.bot.json.api.track;

public class TrackingStatus {

  public String serverID;
  public Status status;
  public String[] players;
  public String modpackVersion;
  public double ms;
  public long lastUpdate;

  public TrackingStatus(
      String serverID,
      Status status,
      String[] players,
      String modpackVersion,
      double ms,
      long lastUpdate) {
    this.serverID = serverID;
    this.status = status;
    this.players = players;
    this.modpackVersion = modpackVersion;
    this.ms = ms;
    this.lastUpdate = lastUpdate;
  }

  public enum Status {
    ONLINE,
    PREINIT,
    INIT,
    POSTINIT,
    STARTING,
    ERRORED,
    STOPPED
  }
}
