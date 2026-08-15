package com.wurmcraft.serveressentials.common.modules.security;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.protect.ConfigProtect;
import com.wurmcraft.serveressentials.common.modules.rank.ConfigRank;
import com.wurmcraft.serveressentials.common.modules.security.event.SecurityEvents;
import joptsimple.internal.Strings;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

@Module(name = "Security")
public class ModuleSecurity {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new SecurityEvents());
    if (TrustedList.trustedUsers.isEmpty()) {
      ServerEssentials.LOG.info(
          "Loading Trusted Users list from '{}'", SecurityEvents.config.trustedList);
      TrustedList.load();
    }
  }

  public void reload() {
    SecurityEvents.config = (ConfigSecurity) SECore.moduleConfigs.get("SECURITY");
    ServerEssentials.LOG.warn(
        "Trusted Users list cannot be reloaded (if there are any users in it! You must restart the server for it to take affect!");
  }

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigSecurity cfg = (ConfigSecurity) SECore.moduleConfigs.get("SECURITY");
    info.add("autoOP: " + cfg.autoOP);
    info.add("checkAlt: " + cfg.checkAlt);
    info.add("lockdownEnabled: " + cfg.lockdownEnabled);
    info.add("modBlacklist: " + Strings.join(cfg.modBlacklist, ", "));
    info.add("preventBlacklistedMods: " + cfg.preventBlacklistedMods);
    return info.toArray(new String[0]);
  }
}
