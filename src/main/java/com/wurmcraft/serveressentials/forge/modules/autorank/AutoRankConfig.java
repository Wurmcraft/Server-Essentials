package com.wurmcraft.serveressentials.forge.modules.autorank;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "AutoRank")
public class AutoRankConfig implements JsonParser {

  // General
  public int checkTimer;
  public boolean announceAutoRank;

  // Rest
  public boolean restAutoSync;
  public int restSyncPeriod;



  public AutoRankConfig() {
    this.restAutoSync = true;
    this.restSyncPeriod = 300;
    this.checkTimer = 24000;
    this.announceAutoRank = true;
  }

  public AutoRankConfig(int checkTimer, boolean announceAutoRank, boolean restAutoSync,
      int restSyncPeriod) {
    this.checkTimer = checkTimer;
    this.announceAutoRank = announceAutoRank;
    this.restAutoSync = restAutoSync;
    this.restSyncPeriod = restSyncPeriod;
  }

  @Override
  public String getID() {
    return "AutoRank";
  }
}
