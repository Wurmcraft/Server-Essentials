package com.wurmcraft.serveressentials.forge.server;

import static com.wurmcraft.serveressentials.forge.modules.utils.CoreUtils.loadGlobalConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.IDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.BasicDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.FileDataHandler;
import com.wurmcraft.serveressentials.forge.server.data.RestDataHandler;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Global.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEssentialsServer {

  public static final Logger LOGGER = LogManager.getLogger(Global.NAME);
  public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient()
      .setPrettyPrinting().create();
  public static ScheduledExecutorService EXECUTORS;

  public static final File SAVE_DIR = new File(Global.NAME.replaceAll(" ", "-"));
  public static boolean isUpdateInProgress = false;

  public ServerEssentialsServer() {
    ModLoadingContext.get().getActiveContainer().registerExtensionPoint(
        ExtensionPoint.DISPLAYTEST, () ->
            Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (s, b) -> true)
    );
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupServer);
    LOGGER.info("Loading has Started");
    SECore.config = loadGlobalConfig();
    SECore.dataHandler = getDataHandler(SECore.config.dataStorageType);
    EXECUTORS = new ScheduledThreadPoolExecutor(SECore.config.supportThreads);
    ModuleLoader.setupModule();
  }

  public void setupServer(FMLCommonSetupEvent e) {
    LOGGER.info("has begun loading");
  }

  @SubscribeEvent
  public static void starting(FMLServerStartingEvent e) {
    SECommand.build(e.getCommandDispatcher());
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
