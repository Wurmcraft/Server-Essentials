package com.wurmcraft.serveressentials.common.modules.economy;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Economy")
public class ConfigEconomy {

    public String serverCurrency;
    public Taxes taxes;

    public ConfigEconomy(String serverCurrency, Taxes taxes) {
        this.serverCurrency = serverCurrency;
        this.taxes = taxes;
    }

    public ConfigEconomy() {
        this.serverCurrency = "default";
        this.taxes = new Taxes();
    }

    public static class Taxes {

        public String[] rankTaxes;
        public double commandTaxMultiplayer;
        public double payTaxMultiplayer;
        public double bankTaxMultiplayer;
        public double incomeTaxMultiplayer;
        public double salesTaxMultiplayer;

        public Taxes(String[] rankTaxes, double commandTaxMultiplayer, double payTaxMultiplayer, double bankTaxMultiplayer, double incomeTaxMultiplayer, double salesTaxMultiplayer) {
            this.rankTaxes = rankTaxes;
            this.commandTaxMultiplayer = commandTaxMultiplayer;
            this.payTaxMultiplayer = payTaxMultiplayer;
            this.bankTaxMultiplayer = bankTaxMultiplayer;
            this.incomeTaxMultiplayer = incomeTaxMultiplayer;
            this.salesTaxMultiplayer = salesTaxMultiplayer;
        }

        public Taxes() {
            this.rankTaxes = new String[]{"*;.1"};
            this.commandTaxMultiplayer = 1;
            this.payTaxMultiplayer = .5;
            this.bankTaxMultiplayer = 2;
            this.incomeTaxMultiplayer = 1;
            this.salesTaxMultiplayer = 1;
        }
    }
}