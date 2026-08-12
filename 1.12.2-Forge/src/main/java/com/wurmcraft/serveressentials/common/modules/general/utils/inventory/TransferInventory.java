package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.TransferEntry;
import com.wurmcraft.serveressentials.api.models.transfer.ItemWrapper;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.ItemStackConverter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TransferInventory extends InventoryBasic {

  public EntityPlayer player;
  public Language lang;
  public TransferEntry entry;
  public int page;

  private int maxPages;
  private ItemStack[] menu;

  public TransferInventory(EntityPlayer player, Language lang, TransferEntry entry, int page) {
    super(
        ChatHelper.replaceColor(lang.DISPLAY_VAULT.replaceAll("\\{@NAME@}", entry.uuid))
            + " | "
            + page,
        true,
        54);
    this.player = player;
    this.entry = entry;
    this.page = page;
    this.lang = lang;
    this.maxPages = entry.items.length / -(54 - 9);
    menu = buildMenu();
  }

  private ItemStack[] buildMenu() {
    ItemStack[] items = new ItemStack[9];
    ItemStack EMPTY = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 8);
    EMPTY.setStackDisplayName("");
    items[0] = EMPTY;
    items[2] = EMPTY;
    items[6] = EMPTY;
    items[8] = EMPTY;
    if (maxPages > 1) {
      // Previous Arrow
      ItemStack prev = new ItemStack(Items.FLINT, 1, 0);
      prev.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_PREV));
      items[1] = prev;
      // Next Arrow
      ItemStack next = new ItemStack(Items.FLINT, 1, 0);
      next.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_NEXT));
      items[7] = next;
    } else {
      items[1] = ItemStack.EMPTY;
      items[7] = ItemStack.EMPTY;
    }
    // Options
    items[3] = ItemStack.EMPTY;
    // Information
    ItemStack info = new ItemStack(Items.KNOWLEDGE_BOOK, 1, 0);
    info.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_INFO));
    NBTTagCompound lore = new NBTTagCompound();
    lore.setString("text", "Name: " + entry.uuid);
    info.getOrCreateSubCompound("display").setTag("Lore", lore);
    items[4] = info;
    items[5] = ItemStack.EMPTY;
    return items;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    if (index > 8 && index < maxPages * 45) {
      int x = (index - 9) + (45 * page);
      if (x < entry.items.length) {
        return convert(entry.items[x]);
      } else return ItemStack.EMPTY;
    }
    if (index < 9) {
      return menu[index];
    }
    return ItemStack.EMPTY;
  }

  public ItemStack convert(ItemWrapper wrapper) {
    String item = wrapper.count + ItemStackConverter.COUNT + wrapper.item;
    if (wrapper.meta != 0) item = item + ItemStackConverter.META + wrapper.meta;
    if (wrapper.nbt != null && !wrapper.nbt.isEmpty())
      item = item + ItemStackConverter.NBT + wrapper.nbt;
    return ServerEssentials.stackConverter.getData(item);
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

  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return index > 8 && index < maxPages * 45;
  }

  @Override
  public ItemStack addItem(ItemStack stack) {
    if (stack.isEmpty()) {
      return ItemStack.EMPTY;
    }
    for (int index = 9; index < (45 * maxPages); index++) {
      ItemStack slot = getStackInSlot(index);
      if (slot.isEmpty()) {
        setInventorySlotContents(index, stack);
        return ItemStack.EMPTY;
      } else if (ItemStack.areItemStacksEqual(slot, stack)) {
        int spaceLeft = slot.getMaxStackSize() - slot.getCount();
        if (spaceLeft >= stack.getCount()) {
          slot.setCount(slot.getCount() + stack.getCount());
          setInventorySlotContents(index, slot);
          return ItemStack.EMPTY;
        } else {
          slot.setCount(slot.getMaxStackSize());
          stack.setCount(stack.getCount() - spaceLeft);
          setInventorySlotContents(index, slot);
        }
      }
    }
    return stack;
  }

  @Override
  public ItemStack removeStackFromSlot(int index) {
    return decrStackSize(index, getStackInSlot(index).getCount());
  }

  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    super.markDirty();
  }

  @Override
  public void markDirty() {
    super.markDirty();
    SECore.dataLoader.update(
        DataLoader.DataType.TRANSFER, String.valueOf(entry.transfer_id), entry);
  }

  @Override
  public boolean isEmpty() {
    for (ItemWrapper item : entry.items) {
      ItemStack stack = convert(item);
      if (!stack.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void openInventory(EntityPlayer player) {
    for (int index = 0; index < getSizeInventory(); index++) {
      setInventorySlotContents(index, getStackInSlot(index));
    }
    entry.is_currently_open = true;
    SECore.dataLoader.update(
        DataLoader.DataType.TRANSFER, String.valueOf(entry.transfer_id), entry);
  }

  @Override
  public void closeInventory(EntityPlayer player) {
    super.closeInventory(player);
    markDirty();
    entry.is_currently_open = false;
    SECore.dataLoader.update(
        DataLoader.DataType.TRANSFER, String.valueOf(entry.transfer_id), entry);
  }

  private void handleAction(int index) {
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
    closeInventory(player);
    player.closeScreen();
    player.displayGUIChest(new TransferInventory(player, lang, entry, page));
  }
}
