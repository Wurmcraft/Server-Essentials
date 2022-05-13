package com.wurmcraft.serveressentials.common.modules.general.command.player;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(
    module = "General",
    name = "Suicide",
    defaultAliases = {"Seppuku"})
public class SuicideCommand {

  @Command(
      args = {},
      usage = {})
  public void suicide(ServerPlayer player) {
    player.player.setHealth(0);
    player.player.setDead();
    ChatHelper.send(player.sender, player.lang.COMMAND_SUICIDE);
  }
}
