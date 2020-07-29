package com.wurmcraft.serveressentials.forge.server.evens;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.Global;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.NoSuchElementException;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

@EventBusSubscriber(modid = Global.MODID)
public class PlayerDataEvents {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlayerLoginEvent(PlayerLoggedInEvent e) {
    try {
      StoredPlayer playerData = (StoredPlayer) SECore.dataHandler.getData(DataKey.PLAYER, e.player.getGameProfile().getId().toString());
    } catch (NoSuchElementException f) {
      PlayerUtils.newPlayer(e.player.getGameProfile().getId().toString(), true);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void onPlayerLogoutEvent(PlayerLoggedOutEvent e) {

  }
}
