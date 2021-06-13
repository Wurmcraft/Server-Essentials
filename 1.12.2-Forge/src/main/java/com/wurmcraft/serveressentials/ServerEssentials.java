package com.wurmcraft.serveressentials;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ServerEssentials.MODID,
        name = ServerEssentials.NAME,
        version = ServerEssentials.VERSION,
        dependencies = "",
        serverSideOnly = true)
public class ServerEssentials {

    public static final String MODID = "server-essentials";
    public static final String NAME = "Server Essentials";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOG = LogManager.getLogger( NAME );

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        LOG.info("Starting Pre-Initialization");
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent e) {
        LOG.info("Starting Initialization");
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent e) {
        LOG.info("Starting Post-Initialization");
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent e) {
        LOG.info("Server Starting has begun");
    }
}
