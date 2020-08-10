package com.wurmcraft.serveressentials.forge.modules.general;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.general.event.GeneralEvents;
import com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.RankConfig;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "General")
public class GeneralModule {

  public static GeneralConfig config;
  public static String[] motd;
  public static String[] rules;
  public static String[] randomMessages;

  public void initSetup() {
    try {
      config = (GeneralConfig) SECore.dataHandler
          .getData(DataKey.MODULE_CONFIG, "General");
    } catch (NoSuchElementException e) {
      File fileLoc = new File(
          SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
              + "General.json");
      if (!fileLoc.getParentFile().exists()) {
        fileLoc.getParentFile().mkdirs();
      }
      try {
        fileLoc.createNewFile();
        Files.write(fileLoc.toPath(), GSON.toJson(new RankConfig()).getBytes());
      } catch (Exception f) {
        ServerEssentialsServer.LOGGER.error(
            "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                + "General");
      }
    }
    motd = GeneralUtils.loadAndCreateConfig("motd");
    rules = GeneralUtils.loadAndCreateConfig("rules");
    randomMessages = GeneralUtils.loadAndCreateConfig("randomMessages");
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new GeneralEvents());
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
