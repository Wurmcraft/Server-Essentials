package com.wurmcraft.serveressentials.forge.modules.rank.event;

import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.rank.RankModule;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class RankEvents {

  @SubscribeEvent(priority = EventPriority.LOW)
  public void userLogin(PlayerLoggedInEvent e) {
    StoredPlayer playerData = PlayerUtils.get(e.player);
    if (playerData.global.rank.isEmpty()) {
      ServerEssentialsServer.LOGGER.warn("User '" + e.player.getDisplayNameString()
          + "' did not have a rank!, Corrected");
      playerData.global.rank = RankModule.config.defaultRank;
    }
  }

  @SubscribeEvent
  public void newPlayer(NewPlayerEvent e) {
    e.newData.global.rank = RankModule.config.defaultRank;
  }

}
