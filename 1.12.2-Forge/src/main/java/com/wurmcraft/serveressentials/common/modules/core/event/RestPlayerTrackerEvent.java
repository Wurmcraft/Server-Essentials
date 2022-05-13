package com.wurmcraft.serveressentials.common.modules.core.event;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.api.event.PlayerUnloadEvent;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class RestPlayerTrackerEvent {

  private static final NonBlockingHashMap<String, ScheduledFuture<?>> userSync =
      new NonBlockingHashMap<>();

  @SubscribeEvent
  public void onPlayerLogin(PlayerLoadEvent e) {
    if (!ServerEssentials.config.performance.useWebsocket)
      userSync.put(
          e.player.getGameProfile().getId().toString(),
          ServerEssentials.scheduledService.scheduleAtFixedRate(
              () -> {
                LOG.debug(
                    "Syncing User '"
                        + e.player.getDisplayNameString()
                        + "' ("
                        + e.player.getGameProfile().getId().toString()
                        + ")");
                if (SECore.dataLoader.delete(
                    DataLoader.DataType.ACCOUNT,
                    e.player.getGameProfile().getId().toString(),
                    true))
                  SECore.dataLoader.get(
                      DataLoader.DataType.ACCOUNT, e.player.getGameProfile().getId().toString());
              },
              ServerEssentials.config.performance.playerSyncInterval,
              ServerEssentials.config.performance.playerSyncInterval,
              TimeUnit.SECONDS));
  }

  @SubscribeEvent
  public void onPlayerUnload(PlayerUnloadEvent e) {
    if (!ServerEssentials.config.performance.useWebsocket && userSync.containsKey(e.account.uuid))
      if (userSync.get(e.account.uuid).cancel(true))
        LOG.debug(
            "Removing Sync for User '"
                + UsernameCache.getLastKnownUsername(UUID.fromString(e.account.uuid))
                + "' ("
                + e.account.uuid
                + ")");
  }
}
