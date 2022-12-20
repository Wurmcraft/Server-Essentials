package com.wurmcraft.serveressentials.common.modules.autorank;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Autorank")
public class ConfigAutorank {

  public boolean announceRackup;

  public ConfigAutorank(boolean announceRackup) {
    this.announceRackup = announceRackup;
  }

  public ConfigAutorank() {
    this.announceRackup = true;
  }
}
