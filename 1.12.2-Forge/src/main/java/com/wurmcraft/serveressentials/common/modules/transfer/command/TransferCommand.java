package com.wurmcraft.serveressentials.common.modules.transfer.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.TransferEntry;
import com.wurmcraft.serveressentials.api.models.transfer.ItemWrapper;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.utils.inventory.TransferInventory;
import com.wurmcraft.serveressentials.common.modules.transfer.ConfigTransfer;
import com.wurmcraft.serveressentials.common.modules.transfer.utils.TransferUtils;
import java.time.Instant;
import java.util.Arrays;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

@ModuleCommand(
    module = "Transfer",
    name = "Transfer",
    defaultAliases = {"tsfr", "tfr", "tf"})
public class TransferCommand {

  public static final String TRANSFER_ID =
      ((ConfigTransfer) SECore.moduleConfigs.get("TRANSFER")).transferID;

  @Command(
      args = {CommandArgument.STRING},
      usage = {"all,hand,hotbar"},
      isSubCommand = true,
      subCommandAliases = {"s"})
  public static void send(ServerPlayer sender, String type) {
    try {
      // TODO Entry Exists
    } catch (Exception e) {
      ItemStack[] items = null;
      InventoryPlayer pInv = sender.player.inventory;
      if (type.equalsIgnoreCase("hand") || type.equalsIgnoreCase("h")) {
        items = new ItemStack[] {pInv.getCurrentItem()};
      } else if (type.equalsIgnoreCase("hotbar")
          || type.equalsIgnoreCase("hot")
          || type.equalsIgnoreCase("bar")
          || type.equalsIgnoreCase("hb")) {
        items = new ItemStack[9];
        for (int index = 0; index < 9; index++) items[index] = pInv.getStackInSlot(index);
      } else if (type.equalsIgnoreCase("all")
          || type.equalsIgnoreCase("*") | type.equalsIgnoreCase("a")) {
        items = new ItemStack[36];
        for (int index = 0; index < 36; index++) items[index] = pInv.getStackInSlot(index);
      }
      TransferEntry entry =
          new TransferEntry(
              -1,
              sender.player.getGameProfile().getId().toString(),
              Instant.now().getEpochSecond(),
              convert(items),
              TRANSFER_ID,
              false);
      SECore.dataLoader.register(DataLoader.DataType.TRANSFER, TRANSFER_ID, entry);
      // TODO Delete Items
      // ChatHelper.send();
    }
  }

  @Command(
      args = {},
      usage = {},
      isSubCommand = true,
      subCommandAliases = {"g"})
  public static void get(ServerPlayer sender) {
    get(sender, 0);
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"page"},
      isSubCommand = true,
      subCommandAliases = {"g"})
  public static void get(ServerPlayer sender, int page) {
    try {
      TransferEntry[] entry =
          TransferUtils.get(TRANSFER_ID, sender.player.getGameProfile().getId().toString());
      if (entry != null && entry.length == 1) {
        if (!entry[0].is_currently_open) {
          sender.player.displayGUIChest(
              new TransferInventory(sender.player, sender.lang, entry[0], page));
        }
      } else {
        // TODO No Entry or corrupted
        //            ChatHelper.send();
      }
    } catch (Exception e) {
      // TODO does not exist
      //            ChatHelper.send();
    }
  }

  public static ItemWrapper[] convert(ItemStack[] items) {
    return Arrays.stream(items)
        .map(item -> new ItemWrapper(ServerEssentials.stackConverter.toString(item)))
        .toArray(ItemWrapper[]::new);
  }
}
