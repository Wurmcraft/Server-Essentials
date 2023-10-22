package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.EcoUtils;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Economy", name = "Pay")
public class PayCommand {

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.DOUBLE},
      usage = {"Player", "Amount"})
  public void pay(ServerPlayer player, EntityPlayer otherPlayer, double amount) {
    String serverCurrency = ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency;
    Account account = PlayerUtils.getLatestAccount(player.global.uuid);
    if (EcoUtils.canBuy(serverCurrency, EcoUtils.computeTax(amount), account)) {
      EcoUtils.buy(account, serverCurrency, EcoUtils.computeTax(amount));
      EcoUtils.earn(account, serverCurrency, amount);
      ChatHelper.send(
          player.sender,
          player
              .lang
              .COMMAND_PAY
              .replaceAll("\\{@AMOUNT@}", "" + amount)
              .replaceAll(
                  "\\{@PLAYER@}",
                  ChatHelper.getName(
                      otherPlayer,
                      Objects.requireNonNull(
                          PlayerUtils.getLatestAccount(
                              otherPlayer.getGameProfile().getId().toString())))));
      Language lang = CommandUtils.getPlayerLang(otherPlayer);
      ChatHelper.send(
          otherPlayer,
          lang.COMMAND_PAY_OTHER
              .replaceAll("\\{@AMOUNT@}", "" + amount)
              .replaceAll("\\{@PLAYER@}", ChatHelper.getName(player.player, account)));
    } else {
      ChatHelper.send(player.sender, player.lang.NO_MONEY);
    }
  }
}
