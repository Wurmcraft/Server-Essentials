package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import com.wurmcraft.serveressentials.api.models.MarketEntry;
import com.wurmcraft.serveressentials.common.modules.economy.MarketHelper;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class ListingsInventory extends InventoryBasic {

  public EntityPlayer player;
  public List<MarketEntry> playerListings = null;

  public ListingsInventory(EntityPlayer player) {
    super("Listings", true, 56);
    this.player = player;
    playerListings = MarketHelper.getPlayerListings(
        player.getGameProfile().getId().toString(), false);
  }

  public ListingsInventory(EntityPlayer player, List<MarketEntry> listings) {
    super("Listings", true, 56);
    this.player = player;
    playerListings = listings;
  }


  @Override
  public ItemStack getStackInSlot(int index) {
    if (playerListings.size() > index) {
      return MarketHelper.getShopDisplayItem(playerListings.get(index),false); // TODO Global Listings
    }
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    handleAction(index);
    return ItemStack.EMPTY;
  }

  // TODO Cancel Listing
  private void handleAction(int index) {
    closeInventory(player);
    player.closeScreen();
    player.displayGUIChest(new ListingsInventory(player, playerListings));
  }
}
