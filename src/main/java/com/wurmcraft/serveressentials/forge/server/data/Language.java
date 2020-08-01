package com.wurmcraft.serveressentials.forge.server.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class Language implements JsonParser {

  public String key;

  // Announcements
  public String ANNOUNCEMENT_NEW_PLAYER;
  public String ANNOUNCEMENT_AUTORANK;

  // Hover Info
  public String HOVER_PLAYER_NAME;

  // Command
  public String COMMAND_USAGE;
  public String COMMAND_SPACER;

  // Core
  public String CORE_SE_RELOAD;
  public String CORE_SE_MODULES;
  public String CORE_SE_VERSION;
  public String CORE_SE_VERSION_REST;
  public String CORE_SE_STORAGE;

  // Rank
  public String RANK_CHANGE;
  public String RANK_CHANGE_SENDER;
  public String RANK_PREFIX;
  public String RANK_SUFFIX;
  public String RANK_PERM_ADD;
  public String RANK_PERM_DEL;
  public String RANK_INHERIT_ADD;
  public String RANK_INHERIT_DEL;
  public String RANK_CREATE;
  public String RANK_DELETE;

  // AutoRank
  public String AUTORANK_AR_NEXT;
  public String AUTORANK_AR_TIME;
  public String AUTORANK_AR_XP;
  public String AUTORANK_AR_MONEY;
  public String AUTORANK_AR_MAX;
  public String AUTORANK_AR_CREATE;
  public String AUTORANK_AR_DELETE;

  public Language(String key) {
    this.key = key;
  }

  @Override
  public String getID() {
    return key;
  }
}
