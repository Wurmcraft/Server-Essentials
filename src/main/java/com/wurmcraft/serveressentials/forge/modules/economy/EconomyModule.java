package com.wurmcraft.serveressentials.forge.modules.economy;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.CurrencyConversion;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.economy.event.AdminSignCommands;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.rank.RankConfig;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Economy")
public class EconomyModule {

  public static EconomyConfig config;

  public void initSetup() {
    try {
      config = (EconomyConfig) SECore.dataHandler
          .getData(DataKey.MODULE_CONFIG, "Economy");
    } catch (NoSuchElementException e) {
      File fileLoc = new File(
          SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
              + "Economy.json");
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
    EcoUtils.checkAndLoadEconomy();
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new AdminSignCommands());
    if (config.restAutoSync) {
      ServerEssentialsServer.EXECUTORS
          .scheduleAtFixedRate(EcoUtils::checkAndLoadEconomy, config.restSyncPeriod,
              config.restSyncPeriod,
              TimeUnit.SECONDS);
    }
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    try {
      for (String rank : SECore.dataHandler
          .getDataFromKey(DataKey.CURRENCY, new CurrencyConversion())
          .keySet()) {
        SECore.dataHandler.delData(DataKey.CURRENCY, rank,
            SECore.config.dataStorageType.equalsIgnoreCase("Rest"));
      }
    } catch (Exception ignored) {
    }
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
