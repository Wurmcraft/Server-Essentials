package com.wurmcraft.bot.json.api.status;


import com.wurmcraft.bot.json.api.json.JsonParser;

public class NetworkTime implements JsonParser {

  public ServerTime[] serverTime;

  public NetworkTime(ServerTime[] serverTime) {
    this.serverTime = serverTime;
  }
}
