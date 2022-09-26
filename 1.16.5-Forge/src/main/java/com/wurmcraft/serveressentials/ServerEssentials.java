package com.wurmcraft.serveressentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ServerEssentials() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void commonSetup(FMLCommonSetupEvent e) {
        MinecraftForge.EVENT_BUS.register(new ServerEssentials());
    }

    public void clientSetup(FMLClientSetupEvent e) {

    }

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent e) {
        CommandDispatcher<CommandSource> commandDispatcher = e.getDispatcher();
    }
}
