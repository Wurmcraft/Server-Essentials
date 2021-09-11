package com.wurmcraft.serveressentials.api.models;

import net.minecraft.item.ItemStack;

public class Kit {

    public String name;
    public String minRank;
    public String[] currencyCosts;
    public String[] rankCooldown;
    public String[] items;
    public String[] armor;

    public Kit(String name, String minRank, String[] currencyCosts, String[] rankCooldown, String[] items, String[] armor) {
        this.name = name;
        this.minRank = minRank;
        this.currencyCosts = currencyCosts;
        this.rankCooldown = rankCooldown;
        this.items = items;
        this.armor = armor;
    }

    public Kit(String name, String[] items, String[] armor) {
        this.name = name;
        this.minRank = "";
        this.currencyCosts = new String[0];
        this.rankCooldown = new String[0];
        this.items = items;
        this.armor = armor;
    }

    public Kit() {
    }
}
