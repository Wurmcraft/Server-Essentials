package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy.Taxes;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(module = "Economy", name = "Taxes", defaultAliases = {"tax"})
public class TaxesCommand {

  @Command(args = {}, usage = {}, canConsoleUse = true)
  public void taxInfo(ServerPlayer player) {
    Taxes taxes = ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).taxes;
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_PAY.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.commandTaxMultiplayer)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_BANK.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.bankTaxMultiplayer)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_COMMAND.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.commandTaxMultiplayer)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_INCOME.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.incomeTaxMultiplayer)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_SALES.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.salesTaxMultiplayer)));
  }

  @Command(args = {CommandArgument.DOUBLE}, usage = {"amount"}, canConsoleUse = true)
  public void taxInfo(ServerPlayer player, double amount) {
    Taxes taxes = ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).taxes;
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_PAY.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.commandTaxMultiplayer * amount)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_BANK.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.bankTaxMultiplayer * amount)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_COMMAND.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.commandTaxMultiplayer * amount)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_INCOME.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.incomeTaxMultiplayer * amount)));
    ChatHelper.send(player.sender, player.lang.COMMAND_TAXES_SALES.replaceAll("\\{@AMOUNT@}", String.format(".2f", taxes.salesTaxMultiplayer * amount)));
  }
}
