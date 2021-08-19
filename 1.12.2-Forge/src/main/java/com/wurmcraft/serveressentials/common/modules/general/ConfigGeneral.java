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

    public ConfigGeneral(String defaultHomeName, int minHomes, int maxHomes, HashMap<String, Location> spawn) {
        this.defaultHomeName = defaultHomeName;
        this.minHomes = minHomes;
        this.maxHomes = maxHomes;
        this.spawn = spawn;
    }

    public ConfigGeneral() {
        this.defaultHomeName = "home";
        this.minHomes = 1;
        this.maxHomes = -1;
        this.spawn = new HashMap<>();
    }
}
