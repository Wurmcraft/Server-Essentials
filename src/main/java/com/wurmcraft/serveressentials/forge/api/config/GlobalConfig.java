package com.wurmcraft.serveressentials.forge.api.config;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class GlobalConfig implements JsonParser {

  public String[] modules;
  public boolean debug;

  public GlobalConfig(String[] modules, boolean debug) {
    this.modules = modules;
    this.debug = debug;
  }

  @Override
  public String getID() {
    return "Global";
  }
}
