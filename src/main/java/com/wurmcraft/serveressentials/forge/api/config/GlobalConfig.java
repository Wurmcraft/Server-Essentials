package com.wurmcraft.serveressentials.forge.api.config;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class GlobalConfig implements JsonParser {

  public String[] modules;
  public boolean debug;
  public String dataStorageType;

  public GlobalConfig() {
    this.modules = new String[0];
    this.debug = false;
    this.dataStorageType = "File";
  }

  public GlobalConfig(String[] modules, boolean debug, String dataStorageType) {
    this.modules = modules;
    this.debug = debug;
    this.dataStorageType = dataStorageType;
  }

  @Override
  public String getID() {
    return "Global";
  }
}
