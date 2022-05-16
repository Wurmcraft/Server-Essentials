package com.wurmcraft.serveressentials.common.modules.autorank;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.AutoRank;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "AutoRank", name = "AutoRank", defaultAliases = {"Ar"})
public class ARCommand {

  @Command(
      args = {},
      usage = {},
      isSubCommand = false,
      subCommandAliases = {},
      canConsoleUse = false
  )
  public void check(ServerPlayer player) {
    displayAutoRankRequirements(player.global, player.sender);
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"},
      isSubCommand = true,
      subCommandAliases = {"c"},
      canConsoleUse = true
  )
  public void check(ServerPlayer player, String other) {
    String uuid = PlayerUtils.getUUIDForInput(other);
    if (uuid != null) {
      Account account = PlayerUtils.getLatestAccount(uuid);
      if (account != null) {
        displayAutoRankRequirements(account, player.sender);
      } else {
        ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
      }
    } else {
      ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
    }
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.INTEGER},
      usage = {"player", "time"},
      isSubCommand = true,
      subCommandAliases = {"c"},
      canConsoleUse = true
  )
  public void create(ServerPlayer player, String rank, String nextRank, int playtime) {
    // TODO Implement
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.INTEGER, CommandArgument.INTEGER},
      usage = {"player", "time"},
      isSubCommand = true,
      subCommandAliases = {"c"},
      canConsoleUse = true
  )
  public void create(ServerPlayer player, String rank, String nextRank, int playtime,
      int amount) {
    // TODO Implement
  }

  private static void displayAutoRankRequirements(Account account,
      ICommandSender sender) {
    List<AutoRank> nextRanks = getNextRanks(account);
    Language lang = null;
    if(sender instanceof EntityPlayer) {
      lang = CommandUtils.getPlayerLang((EntityPlayer) sender);
    } else {
      lang = SECore.dataLoader.get(DataType.LANGUAGE, ((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang, new Language());
    }
    for (AutoRank ar : nextRanks) {
      ChatHelper.send(sender, lang.SPACER);
      // Display Playtime
      if (ar.playtime > 0) {
        ChatHelper.send(sender, lang.COMMAND_AR_TIME.replaceAll("\\{@CURRENT_TIME@}",
                "" + PlayerUtils.getTotalPlaytime(account))
            .replaceAll("\\{@TIME@}", "" + ar.playtime));
      }
      // Display Required Currency
      if(ar.currency_amount > 0) {
        ChatHelper.send(sender, lang.COMMAND_AR_CURRENCY.replaceAll("\\{@CURRENT_AMOUNT@}",
                "" + PlayerUtils.getTotalPlaytime(account))
            .replaceAll("\\{@AMOUNT@}", "" + ar.playtime));
      }
    }
    if(nextRanks.size() == 0) {
      ChatHelper.send(sender, lang.COMMAND_AR_MAX);
    }
  }

  private static List<AutoRank> getNextRanks(Account account) {
    List<AutoRank> nextRanks = new ArrayList<>();
    for(AutoRank ar : SECore.dataLoader.getFromKey(DataType.AUTORANK,new AutoRank()).values()) {
      for(String rank : account.rank)
        if(rank.equals(ar.rank))
          nextRanks.add(ar);
    }
    return nextRanks;
  }
}