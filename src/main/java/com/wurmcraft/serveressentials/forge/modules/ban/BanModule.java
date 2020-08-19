package com.wurmcraft.serveressentials.forge.modules.ban;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.ban.event.BanEvents;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Ban")
public class BanModule {

  public void initSetup() throws NoSuchMethodException {
    if(!SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      ServerEssentialsServer.LOGGER.warn("Ban Module requires `Rest` to function");
      throw new NoSuchMethodException();
    }
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new BanEvents());
  }

  public void reloadModule() throws NoSuchMethodException {
    ServerEssentialsServer.isUpdateInProgress = true;
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }

}
