package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.MarketEntry;
import com.wurmcraft.serveressentials.common.modules.economy.MarketHelper;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

// TODO Implement
public class ShopInventory extends InventoryBasic {

  public EntityPlayer player;
  public Language lang;
  public int page;
  public String filter;
  public boolean global;

  ItemStack[] menu;
  List<MarketEntry> entries;

  public ShopInventory(EntityPlayer player, Language lang, int page) {
    super("", true, 54);
    this.player = player;
    this.lang = lang;
    this.page = page;
    this.filter = "*";
    this.global = false;
    menu = buildMenu();
    entries = MarketHelper.getEntries(global, filter);
  }

  public ShopInventory(EntityPlayer player, Language lang, int page, String filter) {
    super("", true, 54);
    this.player = player;
    this.lang = lang;
    this.page = page;
    this.filter = filter;
    this.global = false;
    menu = buildMenu();
    entries = MarketHelper.getEntries(global, filter);
  }

  public ShopInventory(EntityPlayer player, Language lang, int page, boolean global) {
    super("", true, 54);
    this.player = player;
    this.lang = lang;
    this.page = page;
    this.filter = "*";
    this.global = global;
    menu = buildMenu();
    entries = MarketHelper.getEntries(global, filter);
  }

  public ShopInventory(EntityPlayer player, Language lang, int page, boolean global,
      String filter) {
    super("", true, 54);
    this.player = player;
    this.lang = lang;
    this.page = page;
    this.filter = filter;
    this.global = global;
    menu = buildMenu();
    entries = MarketHelper.getEntries(global, filter);
  }

  private void handleAction(int index) {
    int maxPages = 3; // TODO Max Pages
    if (index == 1) { // Previous Page
      page = page - 1;
      if (page < 0) {
        page = maxPages - 1;
      }
    }
    if (index == 7) { // Next Page
      page = page + 1;
      if (page >= maxPages) {
        page = 0;
      }
    }
    if (index == 5) { // Listings Menu
      closeInventory(player);
      player.closeScreen();
      // TODO Listings Menu
      player.displayGUIChest(new ListingsInventory(player));
      return;
    }
    closeInventory(player);
    player.closeScreen();
    player.displayGUIChest(new ShopInventory(player, lang, page, global, filter));
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    if (index >= 0 && index < 9) {
      return menu[index];
    }
    // TODO Multi Page Support
    index = index - 9;
    if (index < entries.size()) {
      return MarketHelper.getShopDisplayItem(entries.get(index), global);
    }
    return ItemStack.EMPTY;
  }

  private ItemStack[] buildMenu() {
    ItemStack[] items = new ItemStack[9];
    ItemStack EMPTY = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 15);
    EMPTY.setStackDisplayName("");
    items[0] = EMPTY;
    ItemStack prev = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 2);
    prev.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_PREV));
    if (page > 0) {
      items[1] = prev;
    } else {
      items[1] = EMPTY;
    }
    items[2] = EMPTY;
    ItemStack balance = new ItemStack(Items.DIAMOND, 1, 0);
    balance.setStackDisplayName("Balance: %BAL%"); // TODO Lang
    items[3] = balance;
    ItemStack pageNum = new ItemStack(Items.PAPER, 1, 0);
    pageNum.setStackDisplayName("Page: " + page); // TODO Lang
    items[4] = pageNum;
    ItemStack idk = new ItemStack(Items.GOLDEN_APPLE, 1, 1);
    idk.setStackDisplayName("Listings"); // TODO Lang
    items[5] = idk;
    items[6] = EMPTY;
    ItemStack next = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 2);
    next.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_NEXT));
    items[7] = next;
    items[8] = EMPTY;
    return items;
  }

  @Override
  public void closeInventory(EntityPlayer player) {
    super.closeInventory(player);
    markDirty();
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    return decrStackSize(index, getStackInSlot(index).getCount());
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    if (index > 8) {
      ItemStack stack = getStackInSlot(index);
      ItemStack decStack = stack.splitStack(count);
      if (stack.getCount() <= 0) {
        stack = ItemStack.EMPTY;
      }
      setInventorySlotContents(index, stack);
      return decStack;
    }
    handleAction(index);
    return ItemStack.EMPTY;
  }
}
