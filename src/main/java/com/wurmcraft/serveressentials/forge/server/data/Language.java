package com.wurmcraft.serveressentials.forge.server.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class Language implements JsonParser {

  public String key;

  public Language(String key) {
    this.key = key;
  }

  @Override
  public String getID() {
    return key;
  }
}
