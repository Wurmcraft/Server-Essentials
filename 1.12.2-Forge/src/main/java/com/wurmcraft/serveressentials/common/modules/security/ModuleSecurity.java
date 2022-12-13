package com.wurmcraft.serveressentials.common.modules.security;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.modules.security.event.SecurityEvents;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Security")
public class ModuleSecurity {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new SecurityEvents());
    if (TrustedList.trustedUsers.size() == 0) {
      ServerEssentials.LOG.info(
          "Loading Trusted Users list from '" + SecurityEvents.config.trustedList + "'");
      TrustedList.load();
    }
  }

  public void reload() {
    SecurityEvents.config = (ConfigSecurity) SECore.moduleConfigs.get("SECURITY");
    ServerEssentials.LOG.warn(
        "Trusted Users list cannot be reloaded (if there are any users in it! You must restart the server for it to take affect!");
  }
}
