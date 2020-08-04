package com.wurmcraft.serveressentials.forge.api.config;

import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig.Rest;
import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;

public class GlobalConfig implements JsonParser {

  public String[] modules;
  public String serverID;
  public boolean debug;
  public String defaultLang;
  public String langUrlBase;
  public String dataStorageType;
  public int supportThreads;
  public int playerReloadTimeout;
  public LocationWrapper spawn;
  public boolean overrideCommandPerms;
  public boolean logCommandsToConsole;
  public Rest Rest;

  public GlobalConfig() {
    this.modules = new String[0];
    this.debug = false;
    this.dataStorageType = "File";
    this.serverID = "No-Set";
    this.Rest = new Rest();
    this.langUrlBase = "https://raw.githubusercontent.com/Wurmcraft/Server-Essentials-2/1.12/Forge/language";
    this.defaultLang = "en_us";
    this.supportThreads = 2;
    this.playerReloadTimeout = 60;
    this.spawn = null;
    this.overrideCommandPerms = false;
    this.logCommandsToConsole = true;
  }

  public GlobalConfig(String[] modules, String serverID, boolean debug,
      String defaultLang, String langUrlBase, String dataStorageType, int supportThreads,
      int playerReloadTimeout,
      LocationWrapper spawn, boolean overrideCommandPerms, boolean logCommandsToConsole,
      GlobalConfig.Rest rest) {
    this.modules = modules;
    this.serverID = serverID;
    this.debug = debug;
    this.defaultLang = defaultLang;
    this.langUrlBase = langUrlBase;
    this.dataStorageType = dataStorageType;
    this.supportThreads = supportThreads;
    this.playerReloadTimeout = playerReloadTimeout;
    this.spawn = spawn;
    this.overrideCommandPerms = overrideCommandPerms;
    this.logCommandsToConsole = logCommandsToConsole;
    Rest = rest;
  }

  public static class Rest {

    public String restAuth;
    public String restURL;
    public int syncTime;

    public Rest(String restAuth, String restURL, int syncTime) {
      this.restAuth = restAuth;
      this.restURL = restURL;
      this.syncTime = syncTime;
    }

    public Rest() {
      this.restAuth = "";
      this.restURL = "";
      this.syncTime = 90;
    }
  }

  @Override
  public String getID() {
    return "Global";
  }
}
