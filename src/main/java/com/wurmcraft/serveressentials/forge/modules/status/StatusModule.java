package com.wurmcraft.serveressentials.forge.modules.status;

import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.GSON;
import static com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer.SAVE_DIR;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus.Status;
import com.wurmcraft.serveressentials.forge.api.module.Module;
import com.wurmcraft.serveressentials.forge.modules.status.event.StatusEvents;
import com.wurmcraft.serveressentials.forge.modules.status.utils.StatusUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import java.io.File;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Status")
public class StatusModule {

  public static StatusConfig config;

  public void initSetup() {
    try {
      config = (StatusConfig) SECore.dataHandler
          .getData(DataKey.MODULE_CONFIG, "Status");
    } catch (NoSuchElementException e) {
      File fileLoc = new File(
          SAVE_DIR + File.separator + DataKey.MODULE_CONFIG.getName() + File.separator
              + "Status.json");
      if (!fileLoc.getParentFile().exists()) {
        fileLoc.getParentFile().mkdirs();
      }
      try {
        fileLoc.createNewFile();
        Files.write(fileLoc.toPath(), GSON.toJson(new StatusConfig()).getBytes());
      } catch (Exception f) {
        ServerEssentialsServer.LOGGER.error(
            "Failed to create default config for " + DataKey.MODULE_CONFIG + ":"
                + "Status");
      }
    }
  }

  public void finalizeModule() {
    MinecraftForge.EVENT_BUS.register(new StatusEvents());
    if (config.updateType.equalsIgnoreCase("Standard")) {
      ServerEssentialsServer.EXECUTORS
          .scheduleAtFixedRate(() -> StatusUtils.sendUpdate(Status.ONLINE),
              0, config.updatePeriod, TimeUnit.SECONDS);
    } else if (config.updateType
        .equalsIgnoreCase("Aggressive")) { // The schedule doesnt want to play nice :p
      Thread statusThread = new Thread(() -> {
        long lastUpdate = System.currentTimeMillis();
        while (Thread.currentThread().isAlive()) {
          if (lastUpdate + (config.updatePeriod * 1000) < System.currentTimeMillis()) {
            StatusUtils.sendUpdate(Status.ONLINE);
            lastUpdate = System.currentTimeMillis();
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
            }
          }
        }
      }, "Status Tracker");
      statusThread.start();
    }
  }

  public void reloadModule() {
    ServerEssentialsServer.isUpdateInProgress = true;
    initSetup();
    ServerEssentialsServer.isUpdateInProgress = false;
  }
}
