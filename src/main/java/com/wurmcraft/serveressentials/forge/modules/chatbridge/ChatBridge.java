package com.wurmcraft.serveressentials.forge.modules.chatbridge;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.chatbridge.event.ChatEvents;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "ChatBridge", moduleDependencies = {"General", "Language"})
public class ChatBridge {

  public void initSetup() {

  }

  public void finalizeModule() {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      MinecraftForge.EVENT_BUS.register(new ChatEvents());
    } else {
      ServerEssentialsServer.LOGGER
          .error("Unable to start up ChatBridge (Requires Rest)");
    }
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
