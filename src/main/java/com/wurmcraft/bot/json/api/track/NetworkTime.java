package com.wurmcraft.bot.json.api.track;


import com.wurmcraft.bot.json.api.json.JsonParser;

public class NetworkTime implements JsonParser {

  public ServerTime[] serverTime;

  public NetworkTime(ServerTime[] serverTime) {
    this.serverTime = serverTime;
  }
}
