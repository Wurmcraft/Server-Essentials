package com.wurmcraft.serveressentials.forge.modules.rank;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Rank")
public class RankConfig implements JsonParser {

  public String defaultRank;

  public boolean restAutoSync;
  public int restSyncPeriod;

  public RankConfig() {
    this.defaultRank = "Default";
    this.restAutoSync = true;
    this.restSyncPeriod = 300;
  }

  public RankConfig(String defaultRank, boolean restAutoSync, int restSyncPeriod) {
    this.defaultRank = defaultRank;
    this.restAutoSync = restAutoSync;
    this.restSyncPeriod = restSyncPeriod;
  }

  @Override
  public String getID() {
    return "Rank";
  }
}
