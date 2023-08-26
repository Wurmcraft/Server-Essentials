package com.wurmcraft.serveressentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.config.ConfigGlobal;
import com.wurmcraft.serveressentials.common.data.AnnotationLoader;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.FileDataLoader;
import com.wurmcraft.serveressentials.common.data.loader.IDataLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.data.ws.SocketController;
import com.wurmcraft.serveressentials.common.modules.core.command.SECommand;
import com.wurmcraft.serveressentials.common.modules.general.ModuleGeneral;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.TeleportCommand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@Mod(ServerEssentials.MODID)
public class ServerEssentials {

  public static final String MODID = "server-essentials";
  public static final String NAME = "Server Essentials";
  public static final String VERSION = "@VERSION@";

  public static final Logger LOG = LogManager.getLogger("[" + NAME + "]");
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  public static ConfigGlobal config;
  public static ScheduledExecutorService scheduledService;
  public static SocketController socketController;

  public ServerEssentials() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
  }

  private void commonSetup(FMLCommonSetupEvent e) {
    // Initial Setup
    SECore.modules = collectModules();
    config = ConfigLoader.loadGlobalConfig();
    SECore.moduleConfigs = ConfigLoader.loadModuleConfigs();
    scheduledService = Executors.newScheduledThreadPool(config.performance.maxThreads);
    if (SECore.dataLoader instanceof RestDataLoader && config.performance.useWebsocket) {
      socketController = new SocketController();
      if (SECore.moduleConfigs.get("GENERAL") != null) {
        ModuleGeneral.sendStatusUpdate(true, "Starting");
      }
    }
    // General
    setupModules();
    SECore.dataLoader = getDataLoader();
    // Register 'Major' Events
    MinecraftForge.EVENT_BUS.register(ServerEssentials.class);
  }

  public void clientSetup(FMLClientSetupEvent e) {

  }

  @SubscribeEvent
  public static void onRegisterCommandEvent(RegisterCommandsEvent e) {
    CommandDispatcher<CommandSource> commandDispatcher = e.getDispatcher();
    SECommand.register(commandDispatcher);
  }

  /**
   * Creates a hashmap with the loaded modules
   *
   * @return list of sorted modules, [moduleName, module Instance]
   */
  public static NonBlockingHashMap<String, Object> collectModules() {
    StringBuilder builder = new StringBuilder();
    List<Object> modules = AnnotationLoader.loadModules();
    NonBlockingHashMap<String, Object> loadedModules = new NonBlockingHashMap<>();
    for (Object module : modules) {
      Module m = module.getClass().getDeclaredAnnotation(Module.class);
      loadedModules.put(m.name().toUpperCase(), module);
      builder.append(m.name()).append(",");
    }
    String moduleNames = builder.toString();
    if (!moduleNames.isEmpty()) {
      moduleNames = moduleNames.substring(0,
          moduleNames.length() - 1); // Remove trailing ,
    }
    LOG.info("Modules: [" + moduleNames + "]");
    return loadedModules;
  }

  /**
   * Using reflection to call the setup method on each module's instance
   */
  private void setupModules() {
    for (String module : SECore.modules.keySet()) {
      if (isModuleEnabled(module)) {
        Object instance = SECore.modules.get(module);
        Module m = instance.getClass().getDeclaredAnnotation(Module.class);
        try {
          Method method = instance.getClass()
              .getDeclaredMethod(m.setupMethod());
          method.invoke(instance);
        } catch (NoSuchMethodException f) {
          f.printStackTrace();
          LOG.warn("Failed to load module '" + module + "'");
        } catch (InvocationTargetException | IllegalAccessException g) {
          g.printStackTrace();
          LOG.warn("Failed to initialize module '" + module + "'");
        }
      } else {
        LOG.info("Module '" + module + "' exists, but it's not enabled!");
      }
    }
  }

  public static boolean isModuleEnabled(String name) {
    return SECore.modules.keySet().stream().anyMatch(name::equalsIgnoreCase);
  }

  public static IDataLoader getDataLoader() {
    LOG.info("Storage Type: '" + config.storage.storageType + "'");
    if (config.storage.storageType.equalsIgnoreCase("File")) {
      return new FileDataLoader();
    }
    if (config.storage.storageType.equalsIgnoreCase("Rest")) {
      return new RestDataLoader();
    }
    LOG.warn("Failed to load requested storage type, using default 'Cache-Only'");
    return new DataLoader();
  }
}
