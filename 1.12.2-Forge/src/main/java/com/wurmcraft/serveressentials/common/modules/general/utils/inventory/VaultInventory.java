package com.wurmcraft.serveressentials.common.modules.general.utils.inventory;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Vault;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.FileDataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.ItemStackConverter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class VaultInventory extends InventoryBasic {

  public ItemStack[] menu;

  public EntityPlayer player;
  public Language lang;
  public Vault vault;
  public int page;

  public VaultInventory(EntityPlayer player, Language lang, Vault vault, int page) {
    super(
        ChatHelper.replaceColor(lang.DISPLAY_VAULT.replaceAll("\\{@NAME@}", vault.name))
            + " | "
            + page,
        true,
        54);
    this.player = player;
    this.vault = vault;
    this.page = page;
    this.lang = lang;
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
    if (vault.maxPages > 1) {
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
    ItemStack options = new ItemStack(Items.DIAMOND, 1, 0);
    options.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_OPTIONS));
    items[3] = options;
    // Information
    ItemStack info = new ItemStack(Items.KNOWLEDGE_BOOK, 1, 0);
    info.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_INFO));
    NBTTagCompound lore = new NBTTagCompound();
    lore.setString("text", "Name: " + vault.name);
    info.getOrCreateSubCompound("display").setTag("Lore", lore);
    items[4] = info;
    // Purchase / Upgrade
    ItemStack upgrade = new ItemStack(Items.GOLD_INGOT, 1, 0);
    upgrade.setStackDisplayName(ChatHelper.replaceColor(lang.ITEM_UPGRADE));
    items[5] = upgrade;
    return items;
  }

  @Override
  public ItemStack getStackInSlot(int index) {
    if (index > 8 && index < vault.maxPages * 45) {
      int x = (index - 9) + (45 * page);
      return ServerEssentials.stackConverter.getData(vault.items[x]);
    }
    if (index < 9) return menu[index];
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack decrStackSize(int index, int count) {
    if (index > 8) {
      ItemStack stack = getStackInSlot(index);
      ItemStack decStack = stack.splitStack(count);
      if (stack.getCount() <= 0) stack = ItemStack.EMPTY;
      setInventorySlotContents(index, stack);
      return decStack;
    }
    handleAction(index);
    return ItemStack.EMPTY;
  }

  @Override
  public boolean isItemValidForSlot(int index, ItemStack stack) {
    return index > 8 && index < vault.maxPages * 45;
  }

  @Override
  public ItemStack addItem(ItemStack stack) {
    if (stack.isEmpty()) return ItemStack.EMPTY;
    for (int index = 9; index < (45 * vault.maxPages); index++) {
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
    if (index > 8 && index < (45 * vault.maxPages) + 9) {
      if (vault.items.length > index) { // place into existing vault
        vault.items[index - 9 + (page * 45)] = ServerEssentials.stackConverter.toString(stack);
        markDirty();
      } else { // Expand Vault to fit new items
        vault.items = Arrays.copyOf(vault.items, 45 * (index / 45));
        vault.items[index - 9 + +(page * 45)] = ServerEssentials.stackConverter.toString(stack);
        markDirty();
      }
      super.markDirty();
    }
  }

  @Override
  public void markDirty() {
    super.markDirty();
    try {
      File save =
          new File(
              ConfigLoader.SAVE_DIR
                  + File.separator
                  + FileDataLoader.SAVE_FOLDER
                  + File.separator
                  + "vaults"
                  + File.separator
                  + vault.ownerUUID
                  + File.separator
                  + vault.name
                  + ".json");
      if (!save.exists()) {
        save.getParentFile().mkdirs();
        save.createNewFile();
      }
      Files.write(
          save.toPath(),
          GSON.toJson(vault).getBytes(),
          StandardOpenOption.WRITE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (Exception e) {
      LOG.warn("Failed to save vault '" + vault.name + "' for user '" + vault.ownerUUID + "'");
    }
  }

  @Override
  public boolean isEmpty() {
    for (String item : vault.items) {
      ItemStack stack = ServerEssentials.stackConverter.getData(item);
      if (!stack.isEmpty()) return false;
    }
    return true;
  }

  @Override
  public void openInventory(EntityPlayer player) {
    for (int index = 0; index < getSizeInventory(); index++)
      setInventorySlotContents(index, getStackInSlot(index));
  }

  @Override
  public void closeInventory(EntityPlayer player) {
    super.closeInventory(player);
    markDirty();
  }

  private void handleAction(int index) {
    if (index == 1) { // Previous Page
      page = page - 1;
      if (page < 0) page = vault.maxPages - 1;
    }
    if (index == 7) { // Next Page
      page = page + 1;
      if (page >= vault.maxPages) page = 0;
    }
    if (index == 3) { // Options Menu
      // TODO Add Options Menu
      closeInventory(player);
      player.closeScreen();
      player.displayGUIChest(
          new VaultInventory(player, lang, loadVault(vault.ownerUUID, vault.name), page));
      return;
    }
    if (index == 5) { // Upgrade Menu
      // TODO Add Upgrade Menu
      closeInventory(player);
      player.closeScreen();
      player.displayGUIChest(
          new VaultInventory(player, lang, loadVault(vault.ownerUUID, vault.name), page));
      return;
    }
    closeInventory(player);
    player.closeScreen();
    player.displayGUIChest(
        new VaultInventory(player, lang, loadVault(vault.ownerUUID, vault.name), page));
  }

  private Vault loadVault(String ownerUUID, String name) {
    File save =
        new File(
            ConfigLoader.SAVE_DIR
                + File.separator
                + FileDataLoader.SAVE_FOLDER
                + File.separator
                + "vaults"
                + File.separator
                + ownerUUID
                + File.separator
                + name
                + ".json");
    if (save.exists()) {
      try {
        return GSON.fromJson(String.join("\n", Files.readAllLines(save.toPath())), Vault.class);
      } catch (Exception e) {
        LOG.warn("Failed to load vault 'default' for '" + ownerUUID + "'");
        e.printStackTrace();
      }
    }
    return null;
  }
}
