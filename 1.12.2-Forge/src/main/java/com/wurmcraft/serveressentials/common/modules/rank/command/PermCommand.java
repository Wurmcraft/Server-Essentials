package com.wurmcraft.serveressentials.common.modules.rank.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Rank", name = "Perm")
public class PermCommand {

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.RANK},
      usage = {"player", "rank"},
      canConsoleUse = true)
  public void changeRank(ServerPlayer player, EntityPlayer other, Rank rank) {
    changeRank(player, "add", other, rank);
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.PLAYER, CommandArgument.RANK},
      usage = {"type", "player", "rank"},
      canConsoleUse = true)
  public void changeRank(ServerPlayer player, String type, EntityPlayer user, Rank rank) {
    Account oPlayer = PlayerUtils.getLatestAccount(user.getGameProfile().getId().toString());
    if (rank == null) {
      return;
    }
    rank.name = rank.name.toLowerCase();
    if (oPlayer != null) {
      if (type.equalsIgnoreCase("add") || type.equalsIgnoreCase("a")) {
        List<String> ranks = new ArrayList<>();
        Collections.addAll(ranks, oPlayer.rank);
        ranks.add(rank.name);
        ranks = RankUtils.checkForDuplicates(ranks);
        oPlayer.rank = ranks.toArray(new String[0]);
        SECore.dataLoader.update(DataType.ACCOUNT, oPlayer.uuid, oPlayer);
        ChatHelper.send(
            player.sender, player.lang.COMMAND_PERM_ADDED.replaceAll("\\{@RANK@}", rank.name));
      } else if (type.equalsIgnoreCase("remove")
          || type.equalsIgnoreCase("rem")
          || type.equalsIgnoreCase("r")
          || type.equalsIgnoreCase("delete")
          || type.equalsIgnoreCase("del")
          || type.equalsIgnoreCase("d")) {
        List<String> ranks = Arrays.asList(oPlayer.rank);
        ranks.remove(rank.name);
        oPlayer.rank = ranks.toArray(new String[0]);
        SECore.dataLoader.update(DataType.ACCOUNT, oPlayer.uuid, oPlayer);
        ChatHelper.send(
            player.sender, player.lang.COMMAND_PERM_REMOVE.replaceAll("\\{@RANK@}", rank.name));
      } else if (type.equalsIgnoreCase("set") || type.equalsIgnoreCase("s")) {
        oPlayer.rank = new String[] {rank.name};
        SECore.dataLoader.update(DataType.ACCOUNT, oPlayer.uuid, oPlayer);
        ChatHelper.send(
            player.sender, player.lang.COMMAND_PERM_SET.replaceAll("\\{@RANK@}", rank.name));
      } else {
        ChatHelper.send(player.sender, player.lang.COMMAND_PERM_INVALID);
      }
    }
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"player"},
      canConsoleUse = true)
  public void playerRankInfo(ServerPlayer sender, String user) {
    Account oPlayer = PlayerUtils.getLatestAccount(PlayerUtils.getUUIDForInput(user));
    if (oPlayer != null) {
      ChatHelper.send(sender.player, Strings.join(oPlayer.rank, ", "));
    } else {
      ChatHelper.send(sender.player, sender.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", user));
    }
  }
}
