package com.wurmcraft.serveressentials;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.data.DataLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.*;

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

    public static final Logger LOG = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        LOG.info("Starting Pre-Initialization");
        SECore.modules = collectModules();
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


    /**
     * Creates a hashmap with the loaded modules
     *
     * @return list of sorted modules, [moduleName, module Instance]
     */
    public static NonBlockingHashMap<String, Object> collectModules() {
        StringBuilder builder = new StringBuilder();
        List<Object> modules = DataLoader.loadModules();
        NonBlockingHashMap<String, Object> loadedModules = new NonBlockingHashMap<>();
        for (Object module : modules) {
            Module m = module.getClass().getDeclaredAnnotation(Module.class);
            loadedModules.put(m.name().toUpperCase(), module);
            builder.append(m.name()).append(",");
        }
        String moduleNames = builder.toString();
        if (!moduleNames.isEmpty())
            moduleNames = moduleNames.substring(0, moduleNames.length() - 1);   // Remove trailing ,
        LOG.info("Modules: [" + moduleNames + "]");
        return loadedModules;
    }
}
