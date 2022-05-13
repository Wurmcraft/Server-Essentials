package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(
    module = "General",
    name = "Sudo",
    defaultAliases = {"ForceRun"})
public class SudoCommand {

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.STRING},
      usage = {"player", "command"})
  public void sudo(ServerPlayer player, EntityPlayer otherPlayer, String command) {
    FMLCommonHandler.instance()
        .getMinecraftServerInstance()
        .commandManager
        .executeCommand(otherPlayer, "/" + command);
    ChatHelper.send(
        player.sender,
        player
            .lang
            .COMMAND_SUDO
            .replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString())
            .replaceAll("\\{@COMMAND@}", "/" + command));
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.STRING_ARR},
      usage = {"player", "command"})
  public void sudo(ServerPlayer player, EntityPlayer otherPlayer, String[] command) {
    sudo(player, otherPlayer, Strings.join(command, " "));
  }
}
