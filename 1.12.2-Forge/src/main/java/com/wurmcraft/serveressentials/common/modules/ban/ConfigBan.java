package com.wurmcraft.serveressentials.common.modules.ban;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Ban")
public class ConfigBan {

    public boolean followRestBans;

    public ConfigBan(boolean followRestBans) {
        this.followRestBans = followRestBans;
    }

    public ConfigBan() {
        this.followRestBans = true;
    }
}
