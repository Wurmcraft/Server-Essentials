package com.wurmcraft.serveressentials.common.modules.core;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.core.event.PlayerDataTrackerEvent;
import com.wurmcraft.serveressentials.common.modules.core.event.RestPlayerTrackerEvent;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Core", forceAlwaysLoaded = true)
public class ModuleCore {

    public void setup() {
        if (SECore.dataLoader instanceof RestDataLoader) {
            if (((RestDataLoader) SECore.dataLoader).login())
                ServerEssentials.LOG.info("Logged into Rest API as '" + ServerEssentials.config.general.serverID + "'");
            else
                ServerEssentials.LOG.fatal("Failed to login to Rest API");
            MinecraftForge.EVENT_BUS.register(new RestPlayerTrackerEvent());
        }
        MinecraftForge.EVENT_BUS.register(new PlayerDataTrackerEvent());
    }

    public void reload() {

    }
}
