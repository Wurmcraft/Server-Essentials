package com.wurmcraft.serveressentials.forge.modules.rank;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.rank.event.RankEvents;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import net.minecraftforge.common.MinecraftForge;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@Module(name = "Rank")
public class RankModule {

  public static RankConfig config;

  public void initSetup() {
    try {
      config = (RankConfig) SECore.dataHandler.getData(DataKey.MODULE_CONFIG, "Rank");
    } catch (NoSuchElementException e) {
      File fileLoc = new File(
          SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
              + "Rank.json");
      if (!fileLoc.getParentFile().exists()) {
        fileLoc.getParentFile().mkdirs();
      }
      try {
        fileLoc.createNewFile();
        Files.write(fileLoc.toPath(), GSON.toJson(new RankConfig()).getBytes());
      } catch (Exception f) {
        ServerEssentialsServer.LOGGER.error(
            "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                + "Rank");
      }
    }
    NonBlockingHashMap<String, Rank> rankData = new NonBlockingHashMap<>();
    try {
   rankData = SECore.dataHandler.getDataFromKey(DataKey.RANK, new Rank());
    } catch (Exception e) {}
    if (rankData.isEmpty()) {
      ServerEssentialsServer.LOGGER.info("No Ranks found!, Creating Default Ranks");
      RankUtils.createDefaultRanks();
    }
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new RankEvents());
  }

  public void reloadModule() {
    ServerEssentialsServer.isReloadInProgress = true;
    try {
      for (String rank : SECore.dataHandler.getDataFromKey(DataKey.RANK, new Rank()).keySet()) {
        SECore.dataHandler.delData(DataKey.RANK, rank, SECore.config.dataStorageType.equalsIgnoreCase("Rest"));
      }
    } catch (Exception ignored) {}
    initSetup();
    ServerEssentialsServer.isReloadInProgress = false;
  }
}
