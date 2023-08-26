package com.wurmcraft.serveressentials.common.modules.general;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.DataWrapper;
import com.wurmcraft.serveressentials.api.models.ServerStatus;
import com.wurmcraft.serveressentials.api.models.WSWrapper;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.event.PlayerChatEvent;
import com.wurmcraft.serveressentials.common.modules.general.event.GeneralEvents;
import com.wurmcraft.serveressentials.common.modules.general.event.HomeSpawnEvent;
import com.wurmcraft.serveressentials.common.modules.general.event.InventoryTrackingEvents;
import com.wurmcraft.serveressentials.common.modules.general.event.VanishEvent;
import com.wurmcraft.serveressentials.common.modules.security.ConfigSecurity;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

  public void reload() {
  }

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
        LOG.warn("Failed to send updated status to Rest via WebSocket");
        e.printStackTrace();
      }
    } else {
      try {
        RequestGenerator.post("api/information/status", generateStatus(status));
      } catch (Exception e) {
        LOG.warn("Failed to send updated status to Rest via HTTP Post");
        e.printStackTrace();
      }
    }
  }

  public static ServerStatus generateStatus(String status) {
    String[][] playersData = new String[][]{new String[]{}, new String[]{}};
    if (status.equalsIgnoreCase("Online")) {
      playersData = getPlayerInfo();
      if (SECore.modules.contains("SECURITY")
          && ((ConfigSecurity) SECore.moduleConfigs.get("SECURITY")).lockdownEnabled) {
        status = "Lockdown";
      }
    }
    return new ServerStatus(
        ServerEssentials.config.general.serverID,
        computeDelay(),
        Instant.now().getEpochSecond(),
        playersData[0],
        playersData[1],
        status,
        "{}");
  }

  private static long computeDelay() {
    return (long) (getSum(
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
    long timesum = 0L;
    for (long time : times) {
      timesum += time;
    }
    if (times == null) {
      return 0;
    }
    return timesum / times.length;
  }

  public static String[][] getPlayerInfo() {
    List<String> onlinePlayers = new ArrayList<>();
    List<String> playerInfo = new ArrayList<>();
    if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
      for (EntityPlayer player :
          FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
              .getPlayers()) {
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
                + PlayerChatEvent.getRankValue("prefix",
                PlayerChatEvent.getRanks(account))
                + ";");
      }
    }
    return new String[][]{onlinePlayers.toArray(new String[0]),
        playerInfo.toArray(new String[0])};
  }
}
