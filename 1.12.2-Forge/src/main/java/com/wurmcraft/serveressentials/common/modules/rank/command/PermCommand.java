package com.wurmcraft.serveressentials.common.modules.rank.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.Arrays;
import java.util.List;

@ModuleCommand(module = "Rank", name = "Perm")
public class PermCommand {

  @Command(
      args = {CommandArgument.STRING, CommandArgument.RANK},
      usage = {"player", "rank"},
      canConsoleUse = true)
  public void changeRank(ServerPlayer player, String other, Rank rank) {
    changeRank(player, "add", other, rank);
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.RANK},
      usage = {"type", "player", "rank"},
      canConsoleUse = true)
  public void changeRank(ServerPlayer player, String type, String user, Rank rank) {
    Account oPlayer = PlayerUtils.getLatestAccount(PlayerUtils.getUUIDForInput(user));
    if (rank == null) {
      return;
    }
    if (oPlayer != null) {
      if (type.equalsIgnoreCase("add") || type.equalsIgnoreCase("a")) {
        List<String> ranks = Arrays.asList(oPlayer.rank);
        ranks.add(rank.name);
        oPlayer.rank = ranks.toArray(new String[0]);
        SECore.dataLoader.update(DataType.ACCOUNT, oPlayer.uuid, oPlayer);
        ChatHelper.send(player.sender,
            player.lang.COMMAND_PERM_ADDED.replaceAll("\\{@RANK@}", rank.name));
      } else if (type.equalsIgnoreCase("remove") || type.equalsIgnoreCase("rem")
          || type.equalsIgnoreCase("r") || type.equalsIgnoreCase("delete")
          || type.equalsIgnoreCase("del") || type.equalsIgnoreCase("d")) {
        List<String> ranks = Arrays.asList(oPlayer.rank);
        ranks.remove(rank.name);
        oPlayer.rank = ranks.toArray(new String[0]);
        SECore.dataLoader.update(DataType.ACCOUNT, oPlayer.uuid, oPlayer);
        ChatHelper.send(player.sender,
            player.lang.COMMAND_PERM_REMOVE.replaceAll("\\{@RANK@}", rank.name));
      } else if (type.equalsIgnoreCase("set") || type.equalsIgnoreCase("s")) {
        oPlayer.rank = new String[]{rank.name};
        SECore.dataLoader.update(DataType.ACCOUNT, oPlayer.uuid, oPlayer);
        ChatHelper.send(player.sender,
            player.lang.COMMAND_PERM_SET.replaceAll("\\{@RANK@}", rank.name));
      } else {
        ChatHelper.send(player.sender, player.lang.COMMAND_PERM_INVALID);
      }
    } else {
      ChatHelper.send(player.sender,
          player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", user));
    }
  }
}
