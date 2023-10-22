package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.general.event.VanishEvent;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(
    module = "General",
    name = "Vanish",
    defaultAliases = {"V"})
public class VanishCommand {

  @Command(
      args = {},
      usage = {})
  public void vanish(ServerPlayer sender) {
    boolean inVanish = VanishEvent.vanishedPlayers.contains(sender.player);
    if (inVanish) {
      VanishEvent.vanishedPlayers.remove(sender.player);
      VanishEvent.updateVanish(sender.player, true);
      ChatHelper.send(sender.sender, sender.lang.COMMAND_VANISH_UNDO);
    } else {
      VanishEvent.vanishedPlayers.add(sender.player);
      VanishEvent.updateVanish(sender.player, false);
      ChatHelper.send(sender.sender, sender.lang.COMMAND_VANISH);
    }
  }
}
