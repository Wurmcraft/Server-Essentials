package com.wurmcraft.serveressentials.forge.modules.chatbridge;

import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.chatbridge.event.ChatSocketEvents;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;

@Module(name = "ChatBridge", moduleDependencies = {"General", "Language"})
public class ChatBridge {

  public void initSetup() {

  }

  public void finalizeModule() {
    ChatSocketEvents.setup();
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    ChatSocketEvents.setup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
