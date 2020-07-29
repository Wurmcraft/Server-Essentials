package com.wurmcraft.serveressentials.forge.server.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class Language implements JsonParser {

  public String key;

  // Announcements
  public String ANNOUNCEMENT_NEW_PLAYER;

  // Hover Info
  public String HOVER_PLAYER_NAME;


  public Language(String key) {
    this.key = key;
  }

  @Override
  public String getID() {
    return key;
  }
}
