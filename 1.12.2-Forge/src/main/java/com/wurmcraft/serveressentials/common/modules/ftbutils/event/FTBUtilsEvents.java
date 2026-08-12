package com.wurmcraft.serveressentials.common.modules.ftbutils.event;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.ftbutils.FtbUtilsUtils;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class FTBUtilsEvents {

  @SubscribeEvent(priority = EventPriority.LOW)
  public void newPlayer(PlayerLoadEvent e) {
    if (e.newAccount) {
      FtbUtilsUtils.setPlayerClaimBlocks(
          e.player.getGameProfile().getId().toString(),
          FtbUtilsUtils.get(
              e.account,
              PlayerUtils.getUserRanks(e.account).toArray(new Rank[0]),
              new String[] {"ftbutils.claim.", "claimblocks.amount."}),
          FtbUtilsUtils.get(
              e.account,
              PlayerUtils.getUserRanks(e.account).toArray(new Rank[0]),
              new String[] {"ftbutils.chunkloading.", "chunkloading.amount."}));
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
    Account account =
        SECore.dataLoader.get(
            DataLoader.DataType.ACCOUNT,
            e.player.getGameProfile().getId().toString(),
            new Account());
    if (account != null && account.rank != null) {
      FtbUtilsUtils.updatePlayerClaimBlocks(
          e.player,
          FtbUtilsUtils.get(
              account,
              PlayerUtils.getUserRanks(account).toArray(new Rank[0]),
              new String[] {"ftbutils.claim.", "claimblocks.amount."}),
          FtbUtilsUtils.get(
              account,
              PlayerUtils.getUserRanks(account).toArray(new Rank[0]),
              new String[] {"ftbutils.chunkloading.", "chunkloading.amount."}));
    }
  }
}
