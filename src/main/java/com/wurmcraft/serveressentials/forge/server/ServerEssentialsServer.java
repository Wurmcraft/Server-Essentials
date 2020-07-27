package com.wurmcraft.serveressentials.forge.server;

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
  }

  public void setupServer(FMLCommonSetupEvent e) {
    LOGGER.info("has begun loading");
  }
}
