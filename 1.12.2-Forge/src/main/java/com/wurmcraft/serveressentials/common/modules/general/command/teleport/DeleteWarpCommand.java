package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.Warp;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(
    module = "General",
    name = "DeleteWarp",
    defaultAliases = {"DelWarp", "DWarp"})
public class DeleteWarpCommand {

  @Command(
      args = {CommandArgument.WARP},
      usage = {"name"},
      canConsoleUse = true)
  public void delWarp(ServerPlayer player, Warp warp) {
    if (SECore.dataLoader.delete(DataLoader.DataType.WARP, warp.name, false))
      ChatHelper.send(
          player.sender, player.lang.COMMAND_DELWARP.replaceAll("\\{@NAME@}", warp.name));
  }
}
