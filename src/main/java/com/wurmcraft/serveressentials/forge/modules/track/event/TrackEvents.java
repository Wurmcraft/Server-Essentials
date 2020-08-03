package com.wurmcraft.serveressentials.forge.modules.track.event;

import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus.Status;
import com.wurmcraft.serveressentials.forge.modules.track.utils.TrackUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class TrackEvents {

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void playerLoginEvent(PlayerLoggedInEvent e) {
    TrackUtils.sendUpdate(Status.ONLINE);
  }

  @SubscribeEvent
  public void playerLogoutEvent(PlayerLoggedOutEvent e) {
    TrackUtils.sendUpdate(Status.ONLINE);
  }
}
