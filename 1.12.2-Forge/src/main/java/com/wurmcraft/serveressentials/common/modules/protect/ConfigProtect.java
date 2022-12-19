package com.wurmcraft.serveressentials.common.modules.protect;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Protect")
public class ConfigProtect {

  public String defaultType;
  public int defenseRange;
  public int minClaimSize;
  public boolean preventNearbyExplosions;

  public ConfigProtect(String defaultType, int defenseRange, int minClaimSize,
      boolean preventNearbyExplosions) {
    this.defaultType = defaultType;
    this.defenseRange = defenseRange;
    this.minClaimSize = minClaimSize;
    this.preventNearbyExplosions = preventNearbyExplosions;
  }

  public ConfigProtect() {
    this.defaultType = "Basic-2D";
    this.defenseRange = 32;
    this.minClaimSize = 4;
    this.preventNearbyExplosions = false;
  }
}
