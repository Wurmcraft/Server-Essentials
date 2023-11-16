package com.wurmcraft.serveressentials.common.modules.economy;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Economy")
public class ConfigEconomy {

  public String serverCurrency;
  public Taxes taxes;
  public int maxListingsPerCommand;
  public String[] itemBlacklist;
  public int defaultMaxListings;

  public ConfigEconomy(
      String serverCurrency,
      Taxes taxes,
      int maxListingsPerCommand,
      String[] itemBlacklist,
      int defaultMaxListings) {
    this.serverCurrency = serverCurrency;
    this.taxes = taxes;
    this.maxListingsPerCommand = maxListingsPerCommand;
    this.itemBlacklist = itemBlacklist;
    this.defaultMaxListings = defaultMaxListings;
  }

  public ConfigEconomy() {
    this.serverCurrency = "default";
    this.taxes = new Taxes();
    this.maxListingsPerCommand = 6;
    this.itemBlacklist = new String[] {"<bedrock>"};
    this.defaultMaxListings = 10;
  }

  public static class Taxes {

    public String[] rankTaxes;
    public double commandTaxMultiplayer;
    public double payTaxMultiplayer;
    public double bankTaxMultiplayer;
    public double incomeTaxMultiplayer;
    public double salesTaxMultiplayer;

    public Taxes(
        String[] rankTaxes,
        double commandTaxMultiplayer,
        double payTaxMultiplayer,
        double bankTaxMultiplayer,
        double incomeTaxMultiplayer,
        double salesTaxMultiplayer) {
      this.rankTaxes = rankTaxes;
      this.commandTaxMultiplayer = commandTaxMultiplayer;
      this.payTaxMultiplayer = payTaxMultiplayer;
      this.bankTaxMultiplayer = bankTaxMultiplayer;
      this.incomeTaxMultiplayer = incomeTaxMultiplayer;
      this.salesTaxMultiplayer = salesTaxMultiplayer;
    }

    public Taxes() {
      this.rankTaxes = new String[] {"*;.1"};
      this.commandTaxMultiplayer = 1;
      this.payTaxMultiplayer = .5;
      this.bankTaxMultiplayer = 2;
      this.incomeTaxMultiplayer = 1;
      this.salesTaxMultiplayer = 1;
    }
  }
}
