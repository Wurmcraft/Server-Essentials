package com.wurmcraft.serveressentials.common.modules.rank;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Rank")
public class ConfigRank {

  public String defaultRank;

  public ConfigRank(String defaultRank) {
    this.defaultRank = defaultRank;
  }

  public ConfigRank() {
    this.defaultRank = "Default";
  }
}