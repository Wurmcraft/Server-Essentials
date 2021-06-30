package com.wurmcraft.serveressentials.common.modules.core;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Core")
public class ConfigCore {

    public String test;

    public ConfigCore(String test) {
        this.test = test;
    }

    public ConfigCore() {
        this.test = "Test Config";
    }
}
