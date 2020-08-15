package com.wurmcraft.serveressentials.forge.api.json.basic;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class Channel implements JsonParser {

  public String name;
  public String prefix;

  public Channel(String name, String prefix) {
    this.name = name;
    this.prefix = prefix;
  }

  @Override
  public String getID() {
    return name;
  }
}
