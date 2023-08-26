package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.data_wrapper.PerkCost;
import com.wurmcraft.serveressentials.common.command.EcoUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.Arrays;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

@ModuleCommand(module = "Economy", name = "Perk", defaultAliases = {"Perks"})
public class PerkCommand {

  public static NonBlockingHashMap<String, PerkCost> perks = new NonBlockingHashMap<>();

  @Command(args = {}, usage = {}, isSubCommand = true, canConsoleUse = true)
  public void list(ServerPlayer player) {
    ChatHelper.send(player.sender, player.lang.SPACER);
    for (String name : perks.keySet()) {
      ChatHelper.send(player.sender,
          player.lang.COMMAND_PERK_LIST_PERK.replaceAll("\\{@NAME@}", name));
    }
    ChatHelper.send(player.sender, player.lang.SPACER);
  }

  @Command(args = {CommandArgument.STRING}, usage = {
      "perk"}, isSubCommand = true, canConsoleUse = false)
  public void buy(ServerPlayer player, String perk) {
    if(perk == null) {
      ChatHelper.send(player.sender,
          player.lang.COMMAND_PERK_NONE.replaceAll("\\{@NAME@}", "" + perk));
      return;
    }
    int currentLevel = PlayerUtils.getPerkLevel(player.global, perk.toLowerCase());
    PerkCost perkCost = perks.get(perk.toLowerCase());
    if (perkCost != null) {
      double nextCost = (currentLevel - 1) * perkCost.costMultiplier;
      if (currentLevel == 0) {
        nextCost = perkCost.basicCost;
      }
      if (EcoUtils.canBuy(
          ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency, nextCost,
          player.global)) {
        Account account = PlayerUtils.getLatestAccount(
            player.player.getGameProfile().getId().toString());

        // Upgrade Existing
        if (currentLevel > 0) {
          for (int index = 0; index < account.perks.length; index++) {
            if (account.perks[index].startsWith(perkCost.perkNode)) {
              account.perks[index] = perkCost.perkNode + "." + (currentLevel + 1);
              SECore.dataLoader.update(DataType.ACCOUNT, account.uuid, account);
              EcoUtils.buy(account,
                  ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency,
                  nextCost);
              ChatHelper.send(player.sender,
                  player.lang.COMMAND_PERK_BUY.replaceAll("\\{@NAME@}", perkCost.perkNode)
                      .replaceAll("\\{@AMOUNT@}", String.format("%.1f", nextCost)));
              break;
            }
          }
        } else {
          String[] accountPerks = new String[account.perks.length + 1];
          accountPerks = Arrays.copyOfRange(account.perks, 0, account.perks.length);
          accountPerks[account.perks.length] = perkCost.perkNode + ".1";
          account.perks = accountPerks;
          SECore.dataLoader.update(DataType.ACCOUNT, account.uuid, account);
          EcoUtils.buy(account,
              ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency,
              nextCost);
          ChatHelper.send(player.sender,
              player.lang.COMMAND_PERK_BUY.replaceAll("\\{@NAME@}", perkCost.perkNode)
                  .replaceAll("\\{@AMOUNT@}", String.format("%.1f", nextCost)));
        }


      } else {
        ChatHelper.send(player.sender, player.lang.NO_MONEY);
      }
    } else {
      ChatHelper.send(player.sender,
          player.lang.COMMAND_PERK_NONE.replaceAll("\\{@NAME@}", perk));
    }
  }
}
