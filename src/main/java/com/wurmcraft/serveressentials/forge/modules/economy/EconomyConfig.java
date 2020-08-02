package com.wurmcraft.serveressentials.forge.modules.economy;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Economy")
public class EconomyConfig implements JsonParser {


  public Currency defaultCurrency;

  public boolean restAutoSync;
  public int restSyncPeriod;

  public EconomyConfig() {
    this.defaultCurrency = new Currency("Default", 100);
    this.restAutoSync = true;
    this.restSyncPeriod = 300;
  }

  public EconomyConfig(
      Currency defaultCurrency, boolean restAutoSync, int restSyncPeriod) {
    this.defaultCurrency = defaultCurrency;
    this.restAutoSync = restAutoSync;
    this.restSyncPeriod = restSyncPeriod;
  }

  @Override
  public String getID() {
    return "Economy";
  }
}
