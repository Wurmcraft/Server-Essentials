package com.wurmcraft.serveressentials.forge.modules.discord;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.rank.RankConfig;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;

@Module(name = "Discord")
public class DiscordModule {

  public static DiscordConfig config;

  public void initSetup() {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      try {
        config = (DiscordConfig) SECore.dataHandler
            .getData(DataKey.MODULE_CONFIG, "Discord");
      } catch (NoSuchElementException e) {
        File fileLoc = new File(
            SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
                + "Discord.json");
        if (!fileLoc.getParentFile().exists()) {
          fileLoc.getParentFile().mkdirs();
        }
        try {
          fileLoc.createNewFile();
          Files.write(fileLoc.toPath(), GSON.toJson(new DiscordConfig()).getBytes());
        } catch (Exception f) {
          ServerEssentialsServer.LOGGER.error(
              "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                  + "Discord");
        }
      }
    }
  }

  public void finalizeModule() {

  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
