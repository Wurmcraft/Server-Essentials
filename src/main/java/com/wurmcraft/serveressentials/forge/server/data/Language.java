package com.wurmcraft.serveressentials.forge.server.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class Language implements JsonParser {

  public String key;

  // Announcements
  public String ANNOUNCEMENT_NEW_PLAYER;

  // Hover Info
  public String HOVER_PLAYER_NAME;

  // Command
  public String COMMAND_USAGE;

  // Core
  public String CORE_SE_RELOAD;
  public String CORE_SE_MODULES;
  public String CORE_SE_VERSION;
  public String CORE_SE_VERSION_REST;
  public String CORE_SE_STORAGE;


  public Language(String key) {
    this.key = key;
  }

  @Override
  public String getID() {
    return key;
  }
}
