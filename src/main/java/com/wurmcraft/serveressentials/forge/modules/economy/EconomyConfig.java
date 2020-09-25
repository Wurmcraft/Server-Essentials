package com.wurmcraft.serveressentials.forge.modules.economy;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Economy")
public class EconomyConfig implements JsonParser {

  public Currency defaultCurrency;

  public boolean restAutoSync;
  public int restSyncPeriod;
  public int defaultHomeCost;
  public double homeCostMultier;
  public int claimBlockCost;

  public EconomyConfig() {
    this.defaultCurrency = new Currency("Default", 100);
    this.restAutoSync = true;
    this.restSyncPeriod = 300;
    this.defaultHomeCost = 10000;
    this.homeCostMultier = 1.25;
    this.claimBlockCost = 2500;
  }

  public EconomyConfig(
      Currency defaultCurrency, boolean restAutoSync, int restSyncPeriod,
      int defaultHomeCost, double homeCostMultier, int claimBlockCost) {
    this.defaultCurrency = defaultCurrency;
    this.restAutoSync = restAutoSync;
    this.restSyncPeriod = restSyncPeriod;
    this.defaultHomeCost = defaultHomeCost;
    this.homeCostMultier = homeCostMultier;
    this.claimBlockCost = claimBlockCost;
  }

  @Override
  public String getID() {
    return "Economy";
  }
}
