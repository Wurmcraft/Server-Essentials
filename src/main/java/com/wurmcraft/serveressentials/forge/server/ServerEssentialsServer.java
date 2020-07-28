package com.wurmcraft.serveressentials.forge.server;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig;
import com.wurmcraft.serveressentials.forge.server.loader.CommandLoader;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Global.MODID)
public class ServerEssentialsServer {

  public static final Logger LOGGER = LogManager.getLogger(Global.NAME);

  public ServerEssentialsServer() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupServer);
    SECore.config = new GlobalConfig(new String[] {"General"}, true);
  }

  public void setupServer(FMLCommonSetupEvent e) {
    LOGGER.info("has begun loading");
    ModuleLoader.setupModule();
    CommandLoader.setupCommands();
    MinecraftForge.EVENT_BUS.register(new CommandLoader());
  }
}
