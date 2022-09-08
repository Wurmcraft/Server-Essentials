package com.wurmcraft.serveressentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.CommandConfig;
import com.wurmcraft.serveressentials.api.command.CustomCommandJson;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.config.ConfigGlobal;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.CustomCommand;
import com.wurmcraft.serveressentials.common.command.SECommand;
import com.wurmcraft.serveressentials.common.data.AnnotationLoader;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.FileDataLoader;
import com.wurmcraft.serveressentials.common.data.loader.IDataLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.data.ws.SocketController;
import com.wurmcraft.serveressentials.common.modules.general.ModuleGeneral;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@Mod(
    modid = ServerEssentials.MODID,
    name = ServerEssentials.NAME,
    version = ServerEssentials.VERSION,
    dependencies = "",
    serverSideOnly = true,
    acceptableRemoteVersions = "*")
public class ServerEssentials {

  public static final String MODID = "server-essentials";
  public static final String NAME = "Server Essentials";
  public static final String VERSION = "@VERSION@";

  public static final Logger LOG = LogManager.getLogger(NAME);
  public static final Gson GSON =
      new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

  public static ConfigGlobal config;
  public static ScheduledExecutorService scheduledService;
  public static HashMap<Class<?>, CommandConfig> commandClasses;
  public static SocketController socketController;

  @Mod.EventHandler
  public void onPreInit(FMLPreInitializationEvent e) {
    LOG.info("Starting Pre-Initialization");
    config = ConfigLoader.loadGlobalConfig();
    SECore.modules = collectModules();
    SECore.dataLoader = getDataLoader();
    SECore.moduleConfigs = ConfigLoader.loadModuleConfigs();
    scheduledService = Executors.newScheduledThreadPool(config.performance.maxThreads);
    if (SECore.dataLoader instanceof RestDataLoader && config.performance.useWebsocket) {
      socketController = new SocketController();
    }
    if (SECore.moduleConfigs.get("GENERAL") != null
        && SECore.dataLoader.getClass().equals(RestDataLoader.class)) {
      ModuleGeneral.sendStatusUpdate(true, "Pre-Initialization");
    }
  }

  @Mod.EventHandler
  public void onInit(FMLInitializationEvent e) {
    LOG.info("Starting Initialization");
    setupModules();
    if (SECore.moduleConfigs.get("GENERAL") != null
        && SECore.dataLoader.getClass().equals(RestDataLoader.class)) {
      ModuleGeneral.sendStatusUpdate(true, "Initialization");
    }
  }

  /**
   * Using reflection to call the setup method on each module's instance
   */
  private void setupModules() {
    for (String module : SECore.modules.keySet()) {
      Object instance = SECore.modules.get(module);
      Module m = instance.getClass().getDeclaredAnnotation(Module.class);
      try {
        Method method = instance.getClass().getDeclaredMethod(m.setupMethod());
        method.invoke(instance);
      } catch (NoSuchMethodException f) {
        f.printStackTrace();
        LOG.warn("Failed to load module '" + module + "'");
      } catch (InvocationTargetException | IllegalAccessException g) {
        g.printStackTrace();
        LOG.warn("Failed to initialize module '" + module + "'");
      }
    }
  }

  @Mod.EventHandler
  public void onPostInit(FMLPostInitializationEvent e) {
    LOG.info("Starting Post-Initialization");
    commandClasses = loadCommands();
    if (SECore.moduleConfigs.get("GENERAL") != null
        && SECore.dataLoader.getClass().equals(RestDataLoader.class)) {
      ModuleGeneral.sendStatusUpdate(true, "Post-Initialization");
    }
  }

  private HashMap<Class<?>, CommandConfig> loadCommands() {
    List<Class<?>> commands = AnnotationLoader.loadCommands();
    commandClasses = new HashMap<>();
    for (Class<?> command : commands) {
      ModuleCommand instance = command.getDeclaredAnnotation(ModuleCommand.class);
      CommandConfig config = CommandUtils.loadConfig(instance);
      if (config != null) {
        commandClasses.put(command, config);
      } else {
        LOG.warn("Failed to load config for command '" + instance.name() + "'");
      }
    }
    return commandClasses;
  }

  @Mod.EventHandler
  public void onServerStart(FMLServerStartingEvent e) {
    LOG.info("Server Starting has begun");
    for (Class<?> command : commandClasses.keySet()) {
      try {
        e.registerServerCommand(new SECommand(commandClasses.get(command), command));
      } catch (Exception f) {
        f.printStackTrace();
        LOG.warn(
            "Failed to register command '"
                + command.getDeclaredAnnotation(ModuleCommand.class).name()
                + "'");
      }
    }
    // Load Custom Command
    List<CustomCommandJson> customCommands = CustomCommand.loadCustomCommands();
    for (CustomCommandJson json : customCommands) {
      e.registerServerCommand(new CustomCommand(json));
    }
    //         Startup WSS if Rest is enabled
    if (SECore.dataLoader.getClass().equals(RestDataLoader.class)) {
      if (SECore.moduleConfigs.get("GENERAL") != null) {
        ModuleGeneral.sendStatusUpdate(true, "Starting");
      }
    }
  }

  @Mod.EventHandler
  public void serverStarted(FMLServerStartingEvent e) {
    if (SECore.dataLoader.getClass().equals(RestDataLoader.class)) {
      if (SECore.moduleConfigs.get("GENERAL") != null) {
        ModuleGeneral.sendStatusUpdate(true, "Online");
      }
    }
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
