package com.wurmcraft.serveressentials.common.modules.general;

import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.modules.general.event.InventoryTrackingEvents;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "General")
public class ModuleGeneral {

    public void setup() {
        MinecraftForge.EVENT_BUS.register(new InventoryTrackingEvents());
    }

    public void reload() {

    }
}
