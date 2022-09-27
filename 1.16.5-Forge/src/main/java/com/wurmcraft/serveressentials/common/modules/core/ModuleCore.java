package com.wurmcraft.serveressentials.common.modules.core;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.loading.Module;

@Module(name = "Core", forceAlwaysLoaded = true)
public class ModuleCore {

  public void setup() {
    ServerEssentials.LOG.info("Module Core has been loaded!");
  }

  public void reload() {

  }
}
