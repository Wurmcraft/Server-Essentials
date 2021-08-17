package com.wurmcraft.serveressentials.common.modules.general;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "General")
public class ConfigGeneral {

    public String defaultHomeName;
    public int minHomes;
    public int maxHomes;

    public ConfigGeneral(String defaultHomeName, int minHomes, int maxHomes) {
        this.defaultHomeName = defaultHomeName;
        this.minHomes = minHomes;
        this.maxHomes = maxHomes;
    }

    public ConfigGeneral() {
        this.defaultHomeName = "home";
        this.minHomes = 1;
        this.maxHomes = -1;
    }
}
