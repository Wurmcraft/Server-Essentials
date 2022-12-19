package com.wurmcraft.serveressentials.common.modules.general;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;
import com.wurmcraft.serveressentials.api.models.local.Location;
import java.util.HashMap;

@ModuleConfig(module = "General")
public class ConfigGeneral {

  public String defaultHomeName;
  public int minHomes;
  public int maxHomes;
  public HashMap<String, Location> spawn;
  public int rtpRadius;
  public String[] rtpBiomeBlacklist;
  public int[] rtpDimensionWhitelist;
  public String defaultVaultName;
  public int afkCheckTimer;
  public String afkTimer;
  public String playTimeSync;
  public long statusSync;
  public boolean spawnAtHome;

  public ConfigGeneral(String defaultHomeName, int minHomes, int maxHomes,
      HashMap<String, Location> spawn, int rtpRadius, String[] rtpBiomeBlacklist,
      int[] rtpDimensionWhitelist, String defaultVaultName, int afkCheckTimer,
      String afkTimer, String playTimeSync, long statusSync, boolean spawnAtHome) {
    this.defaultHomeName = defaultHomeName;
    this.minHomes = minHomes;
    this.maxHomes = maxHomes;
    this.spawn = spawn;
    this.rtpRadius = rtpRadius;
    this.rtpBiomeBlacklist = rtpBiomeBlacklist;
    this.rtpDimensionWhitelist = rtpDimensionWhitelist;
    this.defaultVaultName = defaultVaultName;
    this.afkCheckTimer = afkCheckTimer;
    this.afkTimer = afkTimer;
    this.playTimeSync = playTimeSync;
    this.statusSync = statusSync;
    this.spawnAtHome = spawnAtHome;
  }

  public ConfigGeneral() {
    this.defaultHomeName = "home";
    this.minHomes = 1;
    this.maxHomes = -1;
    this.spawn = new HashMap<>();
    this.rtpRadius = 3000;
    this.rtpBiomeBlacklist = new String[] {"ocean"};
    this.rtpDimensionWhitelist = new int[] {0, -1};
    this.defaultVaultName = "default";
    this.afkCheckTimer = 30;
    this.afkTimer = "5m";
    this.playTimeSync = "5m";
    this.statusSync = 90;
    this.spawnAtHome = true;
  }
}
