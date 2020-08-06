package com.wurmcraft.serveressentials.forge.modules.core;

import static com.wurmcraft.serveressentials.forge.modules.core.utils.CoreUtils.loadGlobalConfig;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.core.event.CoreEvents;
import com.wurmcraft.serveressentials.forge.modules.core.utils.CoreUtils;
import com.wurmcraft.serveressentials.forge.server.command.json.CommandParamsConfig;
import com.wurmcraft.serveressentials.forge.server.command.json.CustomCommandJson;
import com.wurmcraft.serveressentials.forge.server.json.MessagesConfig;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Core", shouldAllaysBeLoaded = true)
public class CoreModule {

  public static CommandParamsConfig commandConfig;
  public static MessagesConfig messagesConfig;
  public static CustomCommandJson[] customCommands;

  public void initSetup() {
    SECore.config = loadGlobalConfig();
    commandConfig = CoreUtils.loadParamsConfig();
    messagesConfig = CoreUtils.loadMessagesConfig();
    customCommands = CoreUtils.loadCustomCommands();
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new CoreEvents());
  }

  public void reloadModule() {
    initSetup();
  }
}
