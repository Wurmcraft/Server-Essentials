package com.wurmcraft.serveressentials.common.modules.transfer;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;

@Module(
    name = "Transfer",
    dependencies = {"Core"})
public class ModuleTransfer {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {

    } else {
      ServerEssentials.LOG.warn(
          "Module 'Transfer' does not work in 'File' Storage mode!");
      SECore.modules.remove("TRANSFER");
    }
  }

  public void reload() {
  }
}
