package com.wurmcraft.serveressentials.forge.modules.track;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus.Status;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.track.event.TrackEvents;
import com.wurmcraft.serveressentials.forge.modules.track.utils.TrackUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Track")
public class TrackModule {

  public void initSetup() {

  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new TrackEvents());
    ServerEssentialsServer.EXECUTORS
        .scheduleAtFixedRate(() -> TrackUtils.sendUpdate(Status.ONLINE),
            SECore.config.Rest.syncTime, SECore.config.Rest.syncTime, TimeUnit.SECONDS);
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
