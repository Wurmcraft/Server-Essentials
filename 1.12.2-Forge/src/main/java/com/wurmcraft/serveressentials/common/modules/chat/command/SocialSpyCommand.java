package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(
    module = "Chat",
    name = "SocialSpy",
    defaultAliases = {"Ss"})
public class SocialSpyCommand {

  @Command(
      args = {},
      usage = {})
  public void socialSpy(ServerPlayer player) {
    player.local.socialSpy = !player.local.socialSpy;
    SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, player.local.uuid, player.local);
    if (player.local.socialSpy) {
      ChatHelper.send(player.player, player.lang.COMMAND_SOCIALSPY_ENABLED);
      ChatHelper.socialSpy.add(player.player);
    } else {
      ChatHelper.send(player.player, player.lang.COMMAND_SOCIALSPY_DISABLED);
      ChatHelper.socialSpy.remove(player.player);
    }
  }
}
