package com.wurmcraft.serveressentials.common.modules.statistics;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;

@Module(name = "Statistics", dependencies = "Core")
public class ModuleStatistics {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {

    } else {
      ServerEssentials.LOG.warn("Module 'Statistics' does not work in 'File' Storage mode!");
        SECore.modules.remove("STATISTICS");
    }
  }

  public void reload() {}
}
