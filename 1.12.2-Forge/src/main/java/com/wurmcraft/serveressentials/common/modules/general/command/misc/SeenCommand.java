package com.wurmcraft.serveressentials.common.modules.general.command.misc;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.account.ServerTime;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;

@ModuleCommand(
    module = "General",
    name = "Seen",
    defaultAliases = {"LastSeen"})
public class SeenCommand {

  @Command(
      args = {CommandArgument.STRING},
      usage = {"player"},
      canConsoleUse = true)
  public void lookupLastSeen(ServerPlayer player, String otherPlayer) {
    String uuid = PlayerUtils.getUUIDForInput(otherPlayer);
    Account account = PlayerUtils.getLatestAccount(uuid);
    if (uuid != null && !uuid.isEmpty() && account != null) {
      long lastSeen = 0;
      for (ServerTime time : account.tracked_time) {
        if (time.lastSeen > lastSeen) {
          lastSeen = time.lastSeen;
        }
      }
      ChatHelper.send(
          player.sender,
          player
              .lang
              .COMMAND_SEEN
              .replaceAll("\\{@PLAYER@}", PlayerUtils.getUsernameForInput(uuid))
              .replaceAll("\\{@TIME@}", CommandUtils.displayTime(lastSeen / 1000).trim()));
    } else {
      ChatHelper.send(
          player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", otherPlayer));
    }
  }
}
