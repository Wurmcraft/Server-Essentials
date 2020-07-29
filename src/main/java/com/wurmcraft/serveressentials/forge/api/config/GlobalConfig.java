package com.wurmcraft.serveressentials.forge.api.config;

import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig.Rest;
import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class GlobalConfig implements JsonParser {

  public String[] modules;
  public String serverID;
  public boolean debug;
  public String defaultLang;
  public String langUrlBase;
  public String dataStorageType;
  public Rest Rest;

  public GlobalConfig() {
    this.modules = new String[0];
    this.debug = false;
    this.dataStorageType = "File";
    this.serverID = "No-Set";
    this.Rest = new Rest();
    this.defaultLang = "en_us";
  }

  public GlobalConfig(String[] modules, String serverID, boolean debug,
      String defaultLang, String langUrlBase, String dataStorageType,
      GlobalConfig.Rest rest) {
    this.modules = modules;
    this.serverID = serverID;
    this.debug = debug;
    this.defaultLang = defaultLang;
    this.langUrlBase = langUrlBase;
    this.dataStorageType = dataStorageType;
    Rest = rest;
  }

  public static class Rest {

    public String restAuth;
    public String restURL;

    public Rest(String restAuth, String restURL) {
      this.restAuth = restAuth;
      this.restURL = restURL;
    }

    public Rest() {
      this.restAuth = "";
      this.restURL = "";
    }
  }

  @Override
  public String getID() {
    return "Global";
  }
}
