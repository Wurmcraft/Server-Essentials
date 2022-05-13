package com.wurmcraft.serveressentials.common.modules.general.command.item;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.ItemStackConverter;
import net.minecraft.item.ItemStack;

@ModuleCommand(
    module = "General",
    name = "GiveItem",
    defaultAliases = {"Item"})
public class GiveItemCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = {"item"})
  public void getItem(ServerPlayer player, String item) {
    ItemStack stack = ItemStackConverter.getData(item);
    if (stack != null) {
      ChatHelper.send(
          player.sender,
          player
              .lang
              .COMMAND_GIVEITEM
              .replaceAll("\\{@COUNT@}", stack.getCount() + "")
              .replaceAll("\\{@NAME@}", stack.getDisplayName()));
      player.player.inventory.addItemStackToInventory(stack);
    } else ChatHelper.send(player.sender, player.lang.COMMAND_GIVEITEM_NONE);
  }
}
