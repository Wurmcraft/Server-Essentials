package com.wurmcraft.serveressentials.common.modules.general.event;

import com.wurmcraft.serveressentials.common.modules.general.utils.inventory.PlayerInventory;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class InventoryTrackingEvents {

  public static HashMap<EntityPlayer, PlayerInventory> openPlayerInventory = new HashMap<>();

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent e) {
    if (openPlayerInventory.size() > 0 && openPlayerInventory.containsKey(e.player)) {
      openPlayerInventory.get(e.player).update();
    }
  }
}
