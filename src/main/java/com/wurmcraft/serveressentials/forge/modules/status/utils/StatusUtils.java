package com.wurmcraft.serveressentials.forge.modules.status.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus.Status;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.time.Instant;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class StatusUtils {

  public static void sendUpdate(Status status) {
    if(status != Status.ONLINE) {
      RestRequestHandler.Track.updateTrack(
          new ServerStatus(SECore.config.serverID, status, new String[0], 0,
              Instant.now().getEpochSecond()));
    } else {
      RestRequestHandler.Track.updateTrack(
          new ServerStatus(SECore.config.serverID, status, formatPlayers(), calculateMS(),
              Instant.now().getEpochSecond()));
    }
  }

  private static String[] formatPlayers() {
    List<String> playerNames = new ArrayList<>();
    for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
      playerNames.add(formatName(player));
    }
    return playerNames.toArray(new String[0]);
  }

  private static String formatName(EntityPlayer player) {
    StoredPlayer playerData = PlayerUtils.get(player);
    if (!playerData.server.nick.isEmpty()) {
      return playerData.server.nick + " (" + player.getGameProfile().getId().toString()
          + ")";
    } else {
      return player.getDisplayNameString() + " (" + player.getGameProfile().getId()
          .toString() + ")";
    }
  }

  private static double calculateMS() {
    return getSum(
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .worldTickTimes
            .get(
                FMLCommonHandler.instance()
                    .getMinecraftServerInstance()
                    .getWorld(0)
                    .provider
                    .getDimension()))
        * 1.0E-006D;
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

}
