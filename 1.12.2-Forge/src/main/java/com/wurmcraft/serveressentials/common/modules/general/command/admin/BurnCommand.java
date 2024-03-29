package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(
    module = "General",
    name = "Burn",
    defaultAliases = {"Fire", "Ignite"})
public class BurnCommand {

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"},
      canConsoleUse = true)
  public void burnDefaultTime(ServerPlayer player, EntityPlayer otherPlayer) {
    burnTime(player, otherPlayer, 10);
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.INTEGER},
      usage = {"player", "time"},
      canConsoleUse = true)
  public void burnTime(ServerPlayer player, EntityPlayer otherPlayer, int time) {
    otherPlayer.setFire(time);
    ChatHelper.send(
        player.sender,
        player
            .lang
            .COMMAND_FIRE
            .replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString())
            .replaceAll("\\{@TIME@}", "" + time));
  }
}
