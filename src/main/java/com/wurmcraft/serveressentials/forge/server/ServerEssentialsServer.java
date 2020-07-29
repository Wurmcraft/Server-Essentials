package com.wurmcraft.serveressentials.forge.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.data.IDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.BasicDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.FileDataHandler;
import com.wurmcraft.serveressentials.forge.server.loader.CommandLoader;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;

@Mod(modid = Global.MODID, name = Global.NAME, serverSideOnly = true, acceptableRemoteVersions = "*")
public class ServerEssentialsServer {

  public static final Logger LOGGER = LogManager.getLogger(Global.NAME);
  public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient()
      .setPrettyPrinting().create();

  public static final File SAVE_DIR = new File(Global.NAME.replaceAll(" ", "-"));

  @EventHandler
  public void preInit(FMLPreInitializationEvent e) {
    LOGGER.info("Pre-Init has Started");
    SECore.config = setGlobalConfig();
    SECore.dataHandler = getDataHandler(SECore.config.dataStorageType);
    ModuleLoader.setupModule();
    SECore.dataHandler.registerData(DataKey.LANGUAGE, ChatHelper.getDefaultLanguage());
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

  public static IDataHandler getDataHandler(String name) {
    if (name.equalsIgnoreCase("Basic")) {
      return new BasicDataHandler();
    } else if (name.equalsIgnoreCase("File")) {
      return new FileDataHandler();
    } else {
      LOGGER.error("Failed to find '" + name + "' DataHandler!, Defaulting to Basic");
      return new BasicDataHandler();
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
