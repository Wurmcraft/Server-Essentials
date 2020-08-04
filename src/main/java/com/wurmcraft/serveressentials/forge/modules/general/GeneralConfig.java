package com.wurmcraft.serveressentials.forge.modules.general;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "General")
public class GeneralConfig implements JsonParser {

  public int defaultHomeCount;
  public String defaultHomeName;
  public int tpaRequestTimeout;
  public boolean displayMOTDOnJoin;

  public GeneralConfig() {
    this.defaultHomeCount = 1;
    this.defaultHomeName = "home";
    this.tpaRequestTimeout = 150;
    this.displayMOTDOnJoin = true;
  }

  public GeneralConfig(int defaultHomeCount, String defaultHomeName,
      int tpaRequestTimeout,
      boolean displayMOTDOnJoin) {
    this.defaultHomeCount = defaultHomeCount;
    this.defaultHomeName = defaultHomeName;
    this.tpaRequestTimeout = tpaRequestTimeout;
    this.displayMOTDOnJoin = displayMOTDOnJoin;
  }

  @Override
  public String getID() {
    return "General";
  }
}
