package com.wurmcraft.serveressentials.forge.api.json.basic;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class Channel implements JsonParser {

  public String name;
  public String prefix;
  public String discordChannelID;

  public Channel(String name, String prefix) {
    this.name = name;
    this.prefix = prefix;
    this.discordChannelID = "";
  }

  public Channel(String name, String prefix, String discordChannelID) {
    this.name = name;
    this.prefix = prefix;
    this.discordChannelID = discordChannelID;
  }

  @Override
  public String getID() {
    return name;
  }
}
