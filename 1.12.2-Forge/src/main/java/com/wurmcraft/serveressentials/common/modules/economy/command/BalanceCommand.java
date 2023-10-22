package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.account.BankAccount;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(
    module = "Economy",
    name = "Balance",
    defaultAliases = {"Bal"})
public class BalanceCommand {

  @Command(
      args = {},
      usage = {})
  public void balance(ServerPlayer player) {
    Account account =
        PlayerUtils.getLatestAccount(player.player.getGameProfile().getId().toString());
    displayBankInfo(player, account);
  }

  private void displayBankInfo(ServerPlayer player, Account account) {
    ChatHelper.send(player.sender, player.lang.SPACER);
    for (BankAccount bank : account.wallet) {
      ChatHelper.sendTo(
          player.sender,
          player
              .lang
              .COMMAND_BALANCE_BANK_INFO
              .replaceAll("\\{@TYPE@}", bank.accountType)
              .replaceAll("\\{@NAME@}", bank.currencyName)
              .replaceAll("\\{@AMOUNT@}", bank.amount + ""));
    }
    if (account.wallet == null || account.wallet.length == 0) {
      ChatHelper.send(player.sender, player.lang.COMMAND_BALANCE_EMPTY);
    }
    ChatHelper.send(player.sender, player.lang.SPACER);
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void balanceOther(ServerPlayer player, EntityPlayer otherPlayer) {
    Account account = PlayerUtils.getLatestAccount(otherPlayer.getGameProfile().getId().toString());
    displayBankInfo(player, account);
  }
}
