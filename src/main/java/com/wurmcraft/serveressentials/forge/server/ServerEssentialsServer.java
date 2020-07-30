package com.wurmcraft.serveressentials.forge.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.data.IDataHandler;
import com.wurmcraft.serveressentials.forge.api.json.rest.RestValidate;
import com.wurmcraft.serveressentials.forge.server.command.SECommand;
import com.wurmcraft.serveressentials.forge.server.data.BasicDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.FileDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler.Verify;
import com.wurmcraft.serveressentials.forge.server.events.PlayerDataEvents;
import com.wurmcraft.serveressentials.forge.server.loader.CommandLoader;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

@Mod(modid = Global.MODID, name = Global.NAME, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ServerEssentialsServer {

  public static final Logger LOGGER = LogManager.getLogger(Global.NAME);
  public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient()
      .setPrettyPrinting().create();
  public static ScheduledExecutorService EXECUTORS;

  public static final File SAVE_DIR = new File(Global.NAME.replaceAll(" ", "-"));
  public static boolean isReloadInProgress = false;

  @EventHandler
  public void preInit(FMLPreInitializationEvent e) {
    LOGGER.info("Pre-Init has Started");
    SECore.config = setGlobalConfig();
    SECore.dataHandler = getDataHandler(SECore.config.dataStorageType);
    EXECUTORS = new ScheduledThreadPoolExecutor(SECore.config.supportThreads);
    ModuleLoader.setupModule();
    SECore.dataHandler.registerData(DataKey.LANGUAGE, ChatHelper.getDefaultLanguage());
  }

  @EventHandler
  public void init(FMLInitializationEvent e) {
    LOGGER.info("Init has Started");
    CommandLoader.setupCommands();
    MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent e) {
    LOGGER.info("Post-Init has Started");
    if(SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      RestRequestHandler.validate =  RestRequestHandler.Verify.get();
    }
  }

  @EventHandler
  public void onServerStarting(FMLServerStartingEvent e) {
    for (String command : CommandLoader.commands.keySet()) {
      Object commandInstance = CommandLoader.commands.get(command);
      e.registerServerCommand(new SECommand(commandInstance.getClass().getAnnotation(
          ModuleCommand.class), commandInstance));
    }
    // TODO Command Wrapper
  }

  public static IDataHandler getDataHandler(String name) {
    if (name.equalsIgnoreCase("Basic")) {
      return new BasicDataHandler();
    } else if (name.equalsIgnoreCase("File")) {
      return new FileDataHandler();
    } else {
      LOGGER.error("Failed to find '" + name + "' DataHandler!, Defaulting to File!");
      return new FileDataHandler();
    }
  }

  public static GlobalConfig setGlobalConfig() {
    GlobalConfig config;
    try {
      config = GSON.fromJson(Strings.join(Files.readAllLines(
          new File(SAVE_DIR + File.separator + "Global.json").toPath()),
          '\n'), GlobalConfig.class);
      return config;
    } catch (IOException f) {
      config = new GlobalConfig();
      try {
        if (!SAVE_DIR.exists()) {
          SAVE_DIR.mkdirs();
        }
        Files.write(new File(SAVE_DIR + File.separator + "Global.json").toPath(),
            GSON.toJson(config).getBytes());
      } catch (Exception g) {
        ServerEssentialsServer.LOGGER
            .fatal("Failed to save Global.json, (Using default values)");
      }
    }
    return config;
  }
}
