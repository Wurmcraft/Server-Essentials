package com.wurmcraft.serveressentials.common.modules.core;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.modules.core.events.MainPlayerDataEvents;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Core", forceAlwaysLoaded = true)
public class ModuleCore {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new MainPlayerDataEvents());
  }

  public void reload() {

  }
}
