package com.wurmcraft.serveressentials.forge.modules.protect;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.protect.event.ProtectEvents;
import com.wurmcraft.serveressentials.forge.modules.protect.event.ProtectLoadEvents;
import com.wurmcraft.serveressentials.forge.modules.security.SecurityConfig;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Protect")
public class ProtectModule {

  public static ProtectConfig config;

  public void initSetup() {
    try {
      config = (ProtectConfig) SECore.dataHandler
          .getData(DataKey.MODULE_CONFIG, "Protect");
    } catch (NoSuchElementException e) {
      File fileLoc = new File(
          SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
              + "Protect.json");
      if (!fileLoc.getParentFile().exists()) {
        fileLoc.getParentFile().mkdirs();
      }
      try {
        fileLoc.createNewFile();
        Files.write(fileLoc.toPath(), GSON.toJson(new ProtectConfig()).getBytes());
      } catch (Exception f) {
        ServerEssentialsServer.LOGGER.error(
            "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                + "Protect");
      }
    }
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new ProtectLoadEvents());
    MinecraftForge.EVENT_BUS.register(new ProtectEvents());
  }

  public void reloadModule() {

  }
}
