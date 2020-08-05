package com.wurmcraft.serveressentials.forge.server.events;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.PlayerSyncEvent;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.Global;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@EventBusSubscriber(modid = Global.MODID)
public class PlayerDataEvents {

  private static NonBlockingHashMap<UUID, Long> playerLoginTime = new NonBlockingHashMap<>();

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerLoginEvent(PlayerLoggedInEvent e) {
    playerLoginTime.put(e.player.getGameProfile().getId(), System.currentTimeMillis());
    try {
      SECore.dataHandler
          .getData(DataKey.PLAYER, e.player.getGameProfile().getId().toString());
      scheduleFixedUpdate(e);
    } catch (NoSuchElementException f) {
      PlayerUtils.newPlayer(e.player.getGameProfile().getId().toString(), true);
      scheduleFixedUpdate(e);
    }
  }

  private void scheduleFixedUpdate(PlayerLoggedInEvent e) {
    ServerEssentialsServer.EXECUTORS.scheduleAtFixedRate(() -> {
      boolean stillOnline = false;
      for (EntityPlayer player : FMLCommonHandler.instance()
          .getMinecraftServerInstance().getPlayerList().getPlayers()) {
        if (player.getGameProfile().getId().equals(e.player.getGameProfile().getId())) {
          syncPlayerData(e.player.getGameProfile().getId());
          stillOnline = true;
        }
      }
      if (!stillOnline) {
        Thread.currentThread().stop();
      }
    }, 0, SECore.config.Rest.syncTime, TimeUnit.SECONDS);
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void onPlayerLogoutEvent(PlayerLoggedOutEvent e) {
    ServerEssentialsServer.EXECUTORS
        .schedule(() -> {
              syncPlayerData(e.player.getGameProfile().getId());
              playerLoginTime.remove(e.player.getGameProfile().getId());
            }, 0,
            TimeUnit.SECONDS);
    ServerEssentialsServer.EXECUTORS.schedule(() -> {
      SECore.dataHandler
          .delData(DataKey.PLAYER, e.player.getGameProfile().getId().toString(), false);
      if (SECore.config.debug) {
        ServerEssentialsServer.LOGGER
            .info("Unloading '" + e.player.getGameProfile().getId() + "' from storage");
      }
    }, SECore.config.playerReloadTimeout, TimeUnit.SECONDS);
  }

  @SubscribeEvent
  public void onPlayerSync(PlayerSyncEvent e) {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      StoredPlayer mergedData = e.currentData;
      if (mergedData == null) {
        PlayerUtils.newPlayer(e.uuid, false);
      }
      mergedData.global.lastSeen = Instant.now().getEpochSecond();
      mergedData.global.playtime = PlayerUtils
          .syncPlayTime(UUID.fromString(e.currentData.uuid)).global.playtime;
      mergedData.server.lastSeen = Instant.now().getEpochSecond();
      e.loadedPlayer = mergedData;
      if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
        RestRequestHandler.User.overridePlayer(e.otherData.uuid, mergedData.global);
      }
      if (SECore.config.debug) {
        ServerEssentialsServer.LOGGER.info("Syncing Player '" + UsernameCache
            .getLastKnownUsername(UUID.fromString(e.currentData.uuid)) + "'");
      }
    }
  }

  public static long calculatePlaytime(UUID uuid) {
    long lastUpdateTime = playerLoginTime.getOrDefault(uuid, System.currentTimeMillis());
    long amountOfTime = ((System.currentTimeMillis() - lastUpdateTime) / 1000) / 60;
    playerLoginTime.put(uuid, playerLoginTime.get(uuid) + (amountOfTime * (1000 * 60)));
    return amountOfTime;
  }

  public static void syncPlayerData(UUID uuid) {
    GlobalPlayer global = null;
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      global = RestRequestHandler.User.getPlayer(uuid.toString());
    }
    PlayerSyncEvent event = new PlayerSyncEvent(uuid.toString(), PlayerUtils.get(uuid),
        global);
    MinecraftForge.EVENT_BUS.post(event);
    if (event.loadedPlayer != null) {
      SECore.dataHandler.registerData(DataKey.PLAYER, event.loadedPlayer);
    }
  }
}
