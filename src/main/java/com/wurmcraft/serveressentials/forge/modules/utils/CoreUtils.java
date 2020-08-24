package com.wurmcraft.serveressentials.forge.modules.utils;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.logging.log4j.util.Strings;

public class CoreUtils {

  public static GlobalConfig loadGlobalConfig() {
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
