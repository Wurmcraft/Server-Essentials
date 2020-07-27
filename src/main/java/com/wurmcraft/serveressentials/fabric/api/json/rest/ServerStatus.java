package com.wurmcraft.serveressentials.fabric.api.json.rest;

import com.wurmcraft.serveressentials.fabric.api.json.JsonParser;
import java.time.Instant;

/**
 * Info sent to the database for other servers to access about each other
 */
public class ServerStatus implements JsonParser {

  public String serverID;
  public Status status;
  public String[] players;
  public double ms;
  public long lastUpdate;

  public ServerStatus(String serverID,
      Status status, String[] players, double ms) {
    this.serverID = serverID;
    this.status = status;
    this.players = players;
    this.ms = ms;
    this.lastUpdate = Instant.EPOCH.toEpochMilli();
  }

  public ServerStatus(String serverID,
      Status status, String[] players, double ms, long lastUpdate) {
    this.serverID = serverID;
    this.status = status;
    this.players = players;
    this.ms = ms;
    this.lastUpdate = lastUpdate;
  }

  public enum Status {
    ONLINE, STOPPED, STOPPING, PRE_INIT, INIT, POST_INIT, LOADING, ERRORED
  }

  @Override
  public String getID() {
    return serverID;
  }
}
