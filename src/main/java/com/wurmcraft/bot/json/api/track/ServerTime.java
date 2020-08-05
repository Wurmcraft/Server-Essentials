package com.wurmcraft.bot.json.api.track;


import com.wurmcraft.bot.json.api.json.JsonParser;

public class ServerTime implements JsonParser {

  public String serverID;
  public long time;

  public ServerTime(String serverID, long time) {
    this.serverID = serverID;
    this.time = time;
  }
}
