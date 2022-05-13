package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;

public class TrashInventory extends InventoryBasic {

  public EntityPlayer player;

  public TrashInventory(EntityPlayer player, Language lang) {
    super(ChatHelper.replaceColor(lang.DISPLAY_TRASHCAN), true, 54);
    this.player = player;
  }
}
