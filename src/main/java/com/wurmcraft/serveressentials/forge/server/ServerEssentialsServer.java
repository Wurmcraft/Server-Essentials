package com.wurmcraft.serveressentials.forge.server;

import com.wurmcraft.serveressentials.forge.server.loader.CommandLoader;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Global.MODID, name = Global.NAME, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ServerEssentialsServer {

  public static final Logger LOGGER = LogManager.getLogger(Global.NAME);

  @EventHandler
  public void preInit(FMLPreInitializationEvent e) {
    LOGGER.info("Pre-Init has Started");
    ModuleLoader.setupModule();
  }

  @EventHandler
  public void init(FMLInitializationEvent e) {
    LOGGER.info("Init has Started");
    CommandLoader.setupCommands();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent e) {
    LOGGER.info("Post-Init has Started");
  }
}
