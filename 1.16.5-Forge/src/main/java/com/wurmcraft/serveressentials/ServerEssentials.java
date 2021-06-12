package com.wurmcraft.serveressentials;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ServerEssentials.MODID)
public class ServerEssentials {

    public static final String MODID = "server-essentials";
    public static final String NAME = "Server Essentials";
    public static final String VERSION = "@VERSION@";

    public static final Logger LOG = LogManager.getLogger("[" + NAME + "]");

    public ServerEssentials() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void commonSetup(FMLCommonSetupEvent e) {

    }

    public void clientSetup(FMLClientSetupEvent e) {

    }

}
