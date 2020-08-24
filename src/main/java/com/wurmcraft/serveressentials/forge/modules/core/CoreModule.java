package com.wurmcraft.serveressentials.forge.modules.core;

import static com.wurmcraft.serveressentials.forge.modules.utils.CoreUtils.loadGlobalConfig;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;

@Module(name = "Core")
public class CoreModule {

  public void initSetup() {

  }

  public void finalizeModule() {

  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    SECore.config = loadGlobalConfig();
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }

}
