package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(
    module = "General",
    name = "TPADeny",
    defaultAliases = {"TpDeny"})
public class TPADenyCommand {

  @Command(
      args = {},
      usage = {})
  public void acceptTPARequest(ServerPlayer player) {
    if (TPACommand.activeRequests.containsKey(player.player)) {
      EntityPlayer otherPlayer = TPACommand.activeRequests.remove(player.player);
      ChatHelper.send(
          player.player,
          player.lang.COMMAND_TPDENY.replaceAll(
              "\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
    } else if (TPAHereCommand.activeRequests.containsKey(player.player)) {
      EntityPlayer otherPlayer = TPAHereCommand.activeRequests.remove(player.player);
      ChatHelper.send(
          player.player,
          player.lang.COMMAND_TPDENY.replaceAll(
              "\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
    } else {
      ChatHelper.send(player.sender, player.lang.COMMAND_TPACCEPT_NONE);
    }
  }
}
