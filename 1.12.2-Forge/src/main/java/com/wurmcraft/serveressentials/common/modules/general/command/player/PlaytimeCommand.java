package com.wurmcraft.serveressentials.common.modules.general.command.player;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.account.ServerTime;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;

@ModuleCommand(
    module = "General",
    name = "Playtime",
    defaultAliases = {"time", "OnlineTime"})
public class PlaytimeCommand {

  @Command(
      args = {},
      usage = {})
  public void displayPlaytime(ServerPlayer player) {
    ChatHelper.send(player.sender, player.lang.SPACER);
    ChatHelper.send(
        player.sender,
        player.lang.COMMAND_PLAYTIME_TOTAL.replaceAll(
            "\\{@AMOUNT@}", CommandUtils.displayTime(PlayerUtils.getTotalPlaytime(player.global))));
    for (ServerTime time : player.global.tracked_time) {
      ChatHelper.send(
          player.sender,
          player
              .lang
              .COMMAND_PLAYTIME_SPECIFIC
              .replaceAll("\\{@AMOUNT@}", CommandUtils.displayTime(time.totalTime))
              .replaceAll("\\{@SERVER@}", time.serverID));
    }
    ChatHelper.send(player.sender, player.lang.SPACER);
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"Username"})
  public void displayPlaytime(ServerPlayer player, String username) {
    ChatHelper.send(player.sender, player.lang.SPACER);
    Account account =
        SECore.dataLoader.get(
            DataType.ACCOUNT, PlayerUtils.getUUIDForInput(username), new Account());
    if (account != null) {
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_PLAYTIME_TOTAL.replaceAll(
              "\\{@AMOUNT@}", CommandUtils.displayTime(PlayerUtils.getTotalPlaytime(account))));
      for (ServerTime time : account.tracked_time) {
        ChatHelper.send(
            player.sender,
            player
                .lang
                .COMMAND_PLAYTIME_SPECIFIC
                .replaceAll("\\{@AMOUNT@}", CommandUtils.displayTime(time.totalTime))
                .replaceAll("\\{@SERVER@}", time.serverID));
      }
      ChatHelper.send(player.sender, player.lang.SPACER);
    } else {
      ChatHelper.send(
          player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", username));
    }
  }
}
