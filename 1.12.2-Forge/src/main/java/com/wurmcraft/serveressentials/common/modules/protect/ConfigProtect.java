package com.wurmcraft.serveressentials.common.modules.protect;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Protect")
public class ConfigProtect {

    public String defaultType;

    public ConfigProtect(String defaultType) {
        this.defaultType = defaultType;
    }

    public ConfigProtect() {
        this.defaultType = "Basic-2D";
    }
}
