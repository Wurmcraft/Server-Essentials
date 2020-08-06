package com.wurmcraft.serveressentials.forge.server;

import static com.wurmcraft.serveressentials.forge.modules.core.utils.CoreUtils.loadGlobalConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.data.IDataHandler;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus.Status;
import com.wurmcraft.serveressentials.forge.modules.core.CoreModule;
import com.wurmcraft.serveressentials.forge.modules.core.utils.CoreUtils;
import com.wurmcraft.serveressentials.forge.modules.general.command.teleport.HomeCommand;
import com.wurmcraft.serveressentials.forge.modules.track.utils.TrackUtils;
import com.wurmcraft.serveressentials.forge.server.command.CustomCommand;
import com.wurmcraft.serveressentials.forge.server.command.SECommand;
import com.wurmcraft.serveressentials.forge.server.command.WrapperCommand;
import com.wurmcraft.serveressentials.forge.server.data.BasicDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.FileDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.RestDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.events.PlayerDataEvents;
import com.wurmcraft.serveressentials.forge.server.loader.CommandLoader;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Global.MODID, name = Global.NAME, serverSideOnly = true, acceptableRemoteVersions = "*", version = Global.VERSION)
public class ServerEssentialsServer {

  public static final Logger LOGGER = LogManager.getLogger(Global.NAME);
  public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient()
      .setPrettyPrinting().create();
  public static ScheduledExecutorService EXECUTORS;

  public static final File SAVE_DIR = new File(Global.NAME.replaceAll(" ", "-"));
  public static boolean isUpdateInProgress = false;

  @EventHandler
  public void preInit(FMLPreInitializationEvent e) {
    LOGGER.info("Pre-Init has Started");
    SECore.config = loadGlobalConfig();
    SECore.dataHandler = getDataHandler(SECore.config.dataStorageType);
    EXECUTORS = new ScheduledThreadPoolExecutor(SECore.config.supportThreads);
    ModuleLoader.setupModule();
    SECore.dataHandler.registerData(DataKey.LANGUAGE, ChatHelper.getDefaultLanguage());
    if (ModuleLoader.getLoadedModule("Track") != null) {
      TrackUtils.sendUpdate(Status.PRE_INIT);
    }
  }

  @EventHandler
  public void init(FMLInitializationEvent e) {
    LOGGER.info("Init has Started");
    CommandLoader.setupCommands();
    MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
    if (ModuleLoader.getLoadedModule("Track") != null) {
      TrackUtils.sendUpdate(Status.INIT);
    }
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent e) {
    LOGGER.info("Post-Init has Started");
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      RestRequestHandler.validate = RestRequestHandler.Verify.get();
    }
    if (ModuleLoader.getLoadedModule("Track") != null) {
      TrackUtils.sendUpdate(Status.POST_INIT);
    }
  }

  @EventHandler
  public void onServerStarting(FMLServerStartingEvent e) {
    if (SECore.config.overrideCommandPerms) {
      for (String name : e.getServer().getCommandManager().getCommands().keySet()) {
        e.getServer().getCommandManager().getCommands().put(name, new WrapperCommand(
            e.getServer().getCommandManager().getCommands().get(name)));
      }
    }
    for(CustomCommand cmd : CoreUtils.generateCustomCommands()) {
      e.registerServerCommand(cmd);
    }
    for (String command : CommandLoader.commands.keySet()) {
      Object commandInstance = CommandLoader.commands.get(command);
      e.registerServerCommand(new SECommand(commandInstance.getClass().getAnnotation(
          ModuleCommand.class), commandInstance));
    }
    if (ModuleLoader.getLoadedModule("Track") != null) {
      TrackUtils.sendUpdate(Status.LOADING);
    }
  }

  @EventHandler
  public void serverStarted(FMLServerStartedEvent e) {
    if (ModuleLoader.getLoadedModule("Track") != null) {
      TrackUtils.sendUpdate(Status.ONLINE);
    }
  }

  @EventHandler
  public void serverStopping(FMLServerStoppingEvent e) {
    if (ModuleLoader.getLoadedModule("Track") != null) {
      TrackUtils.sendUpdate(Status.STOPPING);
    }
    for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      player.connection.disconnect(
          new TextComponentString(
              CoreModule.messagesConfig.shutdownMessage.replaceAll("&", "\u00A7")));
    }
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent e) {
    if (ModuleLoader.getLoadedModule("Track") != null) {
      TrackUtils.sendUpdate(Status.STOPPED);
    }
  }

  public static IDataHandler getDataHandler(String name) {
    if (name.equalsIgnoreCase("Basic")) {
      return new BasicDataHandler();
    } else if (name.equalsIgnoreCase("File")) {
      return new FileDataHandler();
    } else if (name.equalsIgnoreCase("Rest")) {
      return new RestDataHandler();
    } else {
      LOGGER.error("Failed to find '" + name + "' DataHandler!, Defaulting to File!");
      return new FileDataHandler();
    }
  }
}
