package com.wurmcraft.serveressentials.common.modules.general.command.item;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Kit;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.command.perk.VaultCommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

@ModuleCommand(module = "General", name = "Kit")
public class KitCommand {

  public static String[] INVALID_KIT_NAMES =
      new String[]{"create", "c", "delete", "del", "d", "remove", "rem", "r", "list",
          "l'"};

  @Command(
      args = {CommandArgument.STRING},
      usage = {"name"},
      isSubCommand = true,
      subCommandAliases = {"c"})
  public void create(ServerPlayer player, String name) {
    for (String blacklist : INVALID_KIT_NAMES) {
      if (name.equalsIgnoreCase(blacklist)) {
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_KIT_INVALID.replaceAll("\\{@NAME@}", name));
        return;
      }
    }
    List<String> items = new ArrayList<>();
    String[] armor = new String[4];
    for (int index = 0; index < 4; index++) {
      armor[index] = ServerEssentials.stackConverter.toString(
          player.player.inventory.armorInventory.get(index));
    }
    for (ItemStack item : player.player.inventory.mainInventory) {
      items.add(ServerEssentials.stackConverter.toString(item));
    }
    Kit kit = new Kit(name, items.toArray(new String[0]), armor);
    SECore.dataLoader.register(DataLoader.DataType.KIT, kit.name, kit);
    ChatHelper.send(
        player.sender,
        player.lang.COMMAND_KIT_CREATED.replaceAll("\\{@NAME@}", kit.name));
  }

  @Command(
      args = {},
      usage = {},
      isSubCommand = true,
      subCommandAliases = {"l"},
      canConsoleUse = true)
  public void list(ServerPlayer player) {
    StringBuilder builder = new StringBuilder();
    try {
      for (Kit kit : SECore.dataLoader.getFromKey(DataLoader.DataType.KIT, new Kit())
          .values()) {
        builder.append(kit.name).append(", ");
      }
    } catch (Exception e) {
    }
    ChatHelper.send(
        player.sender,
        player.lang.COMMAND_KIT_LIST.replaceAll("\\{@NAME@}", builder.toString()));
  }

  @Command(
      args = {CommandArgument.KIT},
      usage = {"name"},
      isSubCommand = true,
      subCommandAliases = {"del", "remove", "rem", "r", "d"},
      canConsoleUse = true)
  public void delete(ServerPlayer player, Kit kit) {
    if (kit != null) {
      SECore.dataLoader.delete(DataLoader.DataType.KIT, kit.name, false);
      SECore.dataLoader.delete(DataLoader.DataType.KIT, kit.name, true);
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_KIT_DELETE.replaceAll("\\{@NAME@}", kit.name));
    }
  }

  @Command(
      args = {CommandArgument.KIT},
      usage = {"name"})
  public void kit(ServerPlayer player, Kit kit) {
    if (!RankUtils.isGreaterThan(kit.minRank, player.global.rank)) {
      if (RankUtils.hasPermission(player.global, "command.kit." + kit.name)) {
        if ((player.local.kitUsage != null && player.local.kitUsage.containsKey(
            kit.name))) {
          if (player.local.kitUsage.get(kit.name) < System.currentTimeMillis()) {
            giveKit(player.player, kit, player.global, player.local);
          } else {
            ChatHelper.send(
                player.sender,
                player.lang.COMMAND_KIT_TIMER.replaceAll(
                    "\\{@TIME@}",
                    CommandUtils.displayTime(
                        (player.local.kitUsage.get(kit.name) - System.currentTimeMillis())
                            / 1000)));
          }
        } else {
          giveKit(player.player, kit, player.global, player.local);
        }
      } else {
        ChatHelper.send(player.sender,
            new TextComponentTranslation("commands.generic.permission"));
      }
    } else {
      ChatHelper.send(player.sender,
          new TextComponentTranslation("commands.generic.permission"));
    }
  }

  public static void giveKit(EntityPlayer player, Kit kit, Account global,
      LocalAccount account) {
    // Give kit items to player
    List<ItemStack> unsortedItems = new ArrayList<>();
    for (int index = 0; index < kit.items.length; index++) {
      ItemStack stack = ServerEssentials.stackConverter.getData(kit.items[index]);
      if (stack.isEmpty()) {
        continue;
      }
      if (player.inventory.getStackInSlot(index).isEmpty()) {
        player.inventory.setInventorySlotContents(index, stack);
      } else {
        unsortedItems.add(stack);
      }
    }
    // Armor Items
    for (int index = 0; index < kit.armor.length; index++) {
      ItemStack stack = ServerEssentials.stackConverter.getData(kit.armor[index]);
      if (stack.isEmpty()) {
        continue;
      }
      if (player.inventory.armorInventory.get(index).isEmpty()) {
        player.inventory.armorInventory.set(index, stack);
      } else {
        unsortedItems.add(stack);
      }
    }
    // Give overflow items to player
    for (ItemStack stack : unsortedItems) {
      if (!player.inventory.addItemStackToInventory(stack)) {
        VaultCommand.addToMailbox(player, stack);
      }
    }
    // Update kit usage
    if (account.kitUsage == null) {
      account.kitUsage = new HashMap<>();
    }
    account.kitUsage.put(
        kit.name,
        System.currentTimeMillis() + CommandUtils.getTimeFromConfig(global.rank,
            kit.rankCooldown));
    SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, account.uuid, account);
  }
}
