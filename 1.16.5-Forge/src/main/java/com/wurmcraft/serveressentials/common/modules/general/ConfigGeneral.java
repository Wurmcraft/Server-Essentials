package com.wurmcraft.serveressentials.common.modules.general;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "General")
public class ConfigGeneral {

  public int statusSync;

  public ConfigGeneral(int statusSync) {
    this.statusSync = statusSync;
  }

  public ConfigGeneral() {
    this.statusSync = 120;
  }
}
