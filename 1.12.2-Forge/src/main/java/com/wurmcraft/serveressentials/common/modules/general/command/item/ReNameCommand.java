package com.wurmcraft.serveressentials.common.modules.general.command.item;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;

@ModuleCommand(
    module = "General",
    name = "Rename",
    defaultAliases = {"NickItem", "Name"})
public class ReNameCommand {

  @Command(
      args = {},
      usage = {})
  public void removeName(ServerPlayer player) {
    player
        .player
        .getHeldItemMainhand()
        .setStackDisplayName(
            player.player.getHeldItemMainhand().getItem().getRegistryName()
                .getResourcePath());
    ChatHelper.send(player.sender, player.lang.COMMAND_RENAME_WIPE);
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"name"})
  public void nickItem(ServerPlayer player, String name) {
    player.player.getHeldItemMainhand()
        .setStackDisplayName(ChatHelper.replaceColor(name));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RENAME.replaceAll("\\{@NAME@}", name));
  }

  @Command(
      args = {CommandArgument.STRING_ARR},
      usage = {"name"})
  public void nickItem(ServerPlayer player, String[] name) {
    nickItem(player, Strings.join(name, " "));
  }
}
