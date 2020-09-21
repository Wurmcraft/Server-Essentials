package com.wurmcraft.serveressentials.forge.modules.status;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus.Status;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.status.event.StatusEvents;
import com.wurmcraft.serveressentials.forge.modules.status.utils.StatusUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Status")
public class StatusModule {

  public void initSetup() {

  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new StatusEvents());
    ServerEssentialsServer.EXECUTORS
        .scheduleAtFixedRate(() -> StatusUtils.sendUpdate(Status.ONLINE),
            SECore.config.Rest.syncTime, SECore.config.Rest.syncTime, TimeUnit.SECONDS);
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
