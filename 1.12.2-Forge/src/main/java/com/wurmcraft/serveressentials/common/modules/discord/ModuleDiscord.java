package com.wurmcraft.serveressentials.common.modules.discord;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;

@Module(name = "Discord")
public class ModuleDiscord {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {

    } else {
      ServerEssentials.LOG.warn("Module 'Discord' does not work in 'File' Storage mode!");
      SECore.modules.remove("DISCORD");
    }
  }

  public void reload() {
  }
}
