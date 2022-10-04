package com.wurmcraft.serveressentials.common.modules.core.events;

import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MainPlayerDataEvents {

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerJoin(PlayerLoggedInEvent e) {

  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerLogout(PlayerLoggedOutEvent e) {

  }
}
