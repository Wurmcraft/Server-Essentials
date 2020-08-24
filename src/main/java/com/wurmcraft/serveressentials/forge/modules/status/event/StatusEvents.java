package com.wurmcraft.serveressentials.forge.modules.status.event;

import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus.Status;
import com.wurmcraft.serveressentials.forge.modules.status.utils.StatuUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class StatusEvents {

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void playerLoginEvent(PlayerLoggedInEvent e) {
    StatuUtils.sendUpdate(Status.ONLINE);
  }

  @SubscribeEvent
  public void playerLogoutEvent(PlayerLoggedOutEvent e) {
    StatuUtils.sendUpdate(Status.ONLINE);
  }
}
