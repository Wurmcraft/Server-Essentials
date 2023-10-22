package com.wurmcraft.serveressentials.common.modules.donation;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;

@Module(name = "Donation")
public class ModuleDonation {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {

    } else {
      ServerEssentials.LOG.warn("Module 'Donation' does not work in 'File' Storage mode!");
      SECore.modules.remove("DONATION");
    }
  }

  public void reload() {}
}
