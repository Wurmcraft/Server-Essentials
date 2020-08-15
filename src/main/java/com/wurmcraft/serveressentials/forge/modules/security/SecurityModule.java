package com.wurmcraft.serveressentials.forge.modules.security;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.rank.RankConfig;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.utils.URLUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Module(name = "Security")
public class SecurityModule {

  public static SecurityConfig config;
  public static List<String> trustedUsers = new ArrayList<>();

  public void initSetup() {
    try {
      try {
        config = (SecurityConfig) SECore.dataHandler
            .getData(DataKey.MODULE_CONFIG, "Security");
      } catch (NoSuchElementException e) {
        File fileLoc = new File(
            SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
                + "Security.json");
        if (!fileLoc.getParentFile().exists()) {
          fileLoc.getParentFile().mkdirs();
        }
        try {
          fileLoc.createNewFile();
          Files.write(fileLoc.toPath(), GSON.toJson(new SecurityConfig()).getBytes());
        } catch (Exception f) {
          ServerEssentialsServer.LOGGER.error(
              "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                  + "Security");
        }
      }
      if (!config.trustedUsers.isEmpty()) {
        try {
          String url = URLUtils.readStringFromURL(config.trustedUsers);
          for (String name : url.split("\n")) {
            trustedUsers.add(name);
          }
        } catch (IOException e) {
          ServerEssentialsServer.LOGGER
              .error("Unable to read '" + config.trustedUsers + "'");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void finalizeModule() {

  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    trustedUsers.clear();
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
