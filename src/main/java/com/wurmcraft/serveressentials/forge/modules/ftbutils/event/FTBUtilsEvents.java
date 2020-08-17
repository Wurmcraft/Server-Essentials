package com.wurmcraft.serveressentials.forge.modules.ftbutils.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.ftbutils.utils.FtbUtilsUtils;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class FTBUtilsEvents {

  @SubscribeEvent(priority = EventPriority.LOW)
  public void newPlayer(NewPlayerEvent e) {
    FtbUtilsUtils.setPlayerClaimBlocks(e.newData.uuid,
        FtbUtilsUtils.getMaxClaims(e.newData, (Rank) SECore.dataHandler.getData(
            DataKey.RANK, e.newData.global.rank)),
        FtbUtilsUtils.getMaxChunkLoading(e.newData, (Rank) SECore.dataHandler.getData(
            DataKey.RANK, e.newData.global.rank)));
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void onPlayerLogin(PlayerLoggedInEvent e) {
    StoredPlayer playerData = PlayerUtils
        .get(e.player.getGameProfile().getId().toString());
    if (playerData != null && playerData.global != null
        && playerData.global.rank != null) {
      FtbUtilsUtils.updatePlayerClaimBlocks(e.player,
          FtbUtilsUtils.getMaxClaims(playerData, (Rank) SECore.dataHandler.getData(
              DataKey.RANK, playerData.global.rank)),
          FtbUtilsUtils.getMaxChunkLoading(playerData, (Rank) SECore.dataHandler.getData(
              DataKey.RANK, playerData.global.rank)));
    }
  }
}