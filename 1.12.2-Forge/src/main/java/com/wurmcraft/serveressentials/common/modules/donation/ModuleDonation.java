package com.wurmcraft.serveressentials.common.modules.donation;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.discord.ConfigDiscord;
import joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.List;

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

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigDonation cfg = (ConfigDonation) SECore.moduleConfigs.get("DONATE");
    info.add("donateURL: " + cfg.donateURL);
    return info.toArray(new String[0]);
  }
}
