package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;

public class ListingsInventory extends InventoryBasic {

  public EntityPlayer player;

  public ListingsInventory(EntityPlayer player) {
    super("Listings", true, 56);
    this.player = player;
  }
}
