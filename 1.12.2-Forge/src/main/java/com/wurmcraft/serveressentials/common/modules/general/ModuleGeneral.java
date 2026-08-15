package com.wurmcraft.serveressentials.common.modules.general;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.*;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.general.event.GeneralEvents;
import com.wurmcraft.serveressentials.common.modules.general.event.HomeSpawnEvent;
import com.wurmcraft.serveressentials.common.modules.general.event.InventoryTrackingEvents;
import com.wurmcraft.serveressentials.common.modules.general.event.VanishEvent;
import com.wurmcraft.serveressentials.common.modules.security.ConfigSecurity;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Module(name = "General")
public class ModuleGeneral {

  public static ScheduledFuture<?> statusSchedule;

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new InventoryTrackingEvents());
    MinecraftForge.EVENT_BUS.register(new GeneralEvents());
    MinecraftForge.EVENT_BUS.register(new VanishEvent());
    if (statusSchedule == null && SECore.dataLoader instanceof RestDataLoader) {
      statusSchedule =
          ServerEssentials.scheduledService.scheduleAtFixedRate(
              () -> sendStatusUpdate(true, "Online"),
              ((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).statusSync,
              ((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).statusSync,
              TimeUnit.SECONDS);
    }
    if (((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).spawnAtHome) {
      MinecraftForge.EVENT_BUS.register(new HomeSpawnEvent());
    }
  }

  public void reload() {}

  public static void sendStatusUpdate(boolean useWebSocket, String status) {
    boolean socket = ServerEssentials.config.performance.useWebsocket;
    if (socket && useWebSocket) {
      try {
        ServerEssentials.socketController.send(
            new WSWrapper(
                200,
                WSWrapper.Type.MESSAGE,
                new DataWrapper("Status", GSON.toJson(generateStatus(status)))));
      } catch (Exception e) {
        LOG.warn("Failed to send updated status to Rest via WebSocket ({})", e.getMessage());
      }
    } else {
      try {
        RequestGenerator.post("api/information/status", generateStatus(status));
      } catch (Exception e) {
        LOG.warn("Failed to send updated status to Rest via HTTP Post ({})", e.getMessage());
      }
    }
  }

  public static ServerStatus generateStatus(String status) {
    String[][] playersData = new String[][] {new String[] {}, new String[] {}};
    if (status.equalsIgnoreCase("Online")) {
      playersData = getPlayerInfo();
      if (SECore.modules.contains("SECURITY")
          && ((ConfigSecurity) SECore.moduleConfigs.get("SECURITY")).lockdownEnabled) {
        status = "Lockdown";
      }
      return new ServerStatus(
          ServerEssentials.config.general.serverID,
          computeDelay(),
          Instant.now().getEpochSecond(),
          playersData[0],
          playersData[1],
          status,
          "{}");
    } else {
      return new ServerStatus(
          ServerEssentials.config.general.serverID,
          -1L,
          Instant.now().getEpochSecond(),
          playersData[0],
          playersData[1],
          status,
          "{}");
    }
  }

  private static long computeDelay() {
    return (long)
        (getSum(
                FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .worldTickTimes
                    .get(
                        FMLCommonHandler.instance()
                            .getMinecraftServerInstance()
                            .getWorld(0)
                            .provider
                            .getDimension()))
            * 1.0E-006D);
  }

  private static double getSum(long[] times) {
    if (times == null) {
      return 0;
    }
    long timesum = 0L;
    for (long time : times) {
      timesum += time;
    }
    return (double) timesum / times.length;
  }

  public static String[][] getPlayerInfo() {
    List<String> onlinePlayers = new ArrayList<>();
    List<String> playerInfo = new ArrayList<>();
    if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
      for (EntityPlayer player :
          FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
        if (VanishEvent.vanishedPlayers.contains(player)) {
          continue;
        }
        onlinePlayers.add(player.getGameProfile().getId().toString());
        Account account =
            SECore.dataLoader.get(
                DataLoader.DataType.ACCOUNT,
                player.getGameProfile().getId().toString(),
                new Account());
        playerInfo.add(
            player.getDisplayNameString()
                + ";"
                + PlayerChatEvent.getRankValue("prefix", PlayerChatEvent.getRanks(account))
                + ";");
      }
    }
    return new String[][] {onlinePlayers.toArray(new String[0]), playerInfo.toArray(new String[0])};
  }

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigGeneral cfg = (ConfigGeneral) SECore.moduleConfigs.get("GENERAL");
    info.add("defaultHomeName: " + cfg.defaultHomeName);
    info.add("afkTimer: " + cfg.afkTimer);
    info.add("defaultVaultName: " + cfg.defaultVaultName);
    info.add("globalMOTD: " + cfg.globalMOTD);
    info.add("playTimeSync: " + cfg.playTimeSync);
    info.add("afkCheckTimer: " + cfg.afkCheckTimer);
    info.add("maxHomes: " + cfg.maxHomes);
    info.add("minHomes: " + cfg.minHomes);
    info.add("notifyMailboxItems: " + cfg.notifyMailboxItems);
    info.add("rtpBiomeBlacklist: " + Strings.join(cfg.rtpBiomeBlacklist, ", "));
    info.add("rtpDimensionWhitelist: " + Arrays.toString(cfg.rtpDimensionWhitelist));
    info.add("rtpRadius: " + cfg.rtpRadius);
    info.add("spawnAtHome: " + cfg.spawnAtHome);
    info.add("statusSync: " + cfg.statusSync);
    info.add("vaultPageBaseCost: " + cfg.vaultPageBaseCost);
    info.add("vaultPageCostMultiplier: " + cfg.vaultPageCostMultiplier);
    info.add("Vanished: " + VanishEvent.vanishedPlayers.size());
    info.add("Menu Inventory Open: " + InventoryTrackingEvents.openPlayerInventory.size());
    info.add("AFK Players: " + GeneralEvents.afkPlayers.size());
    info.add("Frozen Players: " + GeneralEvents.frozenPlayers.size());
    return info.toArray(new String[0]);
  }
}
