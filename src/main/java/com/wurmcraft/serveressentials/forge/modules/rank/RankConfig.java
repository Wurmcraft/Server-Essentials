package com.wurmcraft.serveressentials.forge.modules.rank;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Rank")
public class RankConfig implements JsonParser {

  public String defaultRank;

  public RankConfig() {
    this.defaultRank = "Default";
  }

  public RankConfig(String defaultRank) {
    this.defaultRank = defaultRank;
  }

  @Override
  public String getID() {
    return "Rank";
  }
}
