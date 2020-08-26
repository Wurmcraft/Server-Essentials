package com.wurmcraft.serveressentials.forge.modules.autorank;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.autorank.event.AutoRankEvents;
import com.wurmcraft.serveressentials.forge.modules.autorank.utils.AutoRankUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.RankConfig;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "AutoRank", moduleDependencies = {"Rank"})
public class AutoRankModule {

  public static AutoRankConfig config;

  public void initSetup() {
    try {
      try {
        config = (AutoRankConfig) SECore.dataHandler
            .getData(DataKey.MODULE_CONFIG, "AutoRank");
      } catch (NoSuchElementException e) {
        File fileLoc = new File(
            SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
                + "AutoRank.json");
        if (!fileLoc.getParentFile().exists()) {
          fileLoc.getParentFile().mkdirs();
        }
        try {
          fileLoc.createNewFile();
          Files.write(fileLoc.toPath(), GSON.toJson(new AutoRankConfig()).getBytes());
        } catch (Exception f) {
          ServerEssentialsServer.LOGGER.error(
              "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                  + "AutoRank");
        }
      }
      AutoRankUtils.checkAndLoadAutoRanks();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new AutoRankEvents());
    if (config.restAutoSync) {
      ServerEssentialsServer.EXECUTORS
          .scheduleAtFixedRate(AutoRankUtils::checkAndLoadAutoRanks,
              config.restSyncPeriod,
              config.restSyncPeriod,
              TimeUnit.SECONDS);
    }
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    try {
      for (String rank : SECore.dataHandler
          .getDataFromKey(DataKey.AUTO_RANK, new AutoRank())
          .keySet()) {
        SECore.dataHandler.delData(DataKey.AUTO_RANK, rank,
            SECore.config.dataStorageType.equalsIgnoreCase("Rest"));
      }
    } catch (Exception ignored) {
    }
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }

}
