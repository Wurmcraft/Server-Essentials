package com.wurmcraft.serveressentials.forge.modules.economy.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "Economy", name = "Eco")
public class EcoCommand {

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING,
      CommandArguments.INTEGER}, inputNames = {"Player", "Add, Rem", "Amount"})
  public void changeInt(ICommandSender sender, EntityPlayer player, String type,
      int amount) {
    changeIntCustom(sender, player, type, amount,
        EconomyModule.config.defaultCurrency.name);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING,
      CommandArguments.DOUBLE}, inputNames = {"Player", "Add, Rem", "Amount"})
  public void changeDouble(ICommandSender sender, EntityPlayer player, String type,
      double amount) {
    changeDoubleCustom(sender, player, type, amount,
        EconomyModule.config.defaultCurrency.name);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING,
      CommandArguments.INTEGER, CommandArguments.STRING}, inputNames = {"Player",
      "Add, Rem", "Amount", "Currency"})
  public void changeIntCustom(ICommandSender sender, EntityPlayer player, String type,
      int amount, String currency) {
    changeDoubleCustom(sender, player, type, amount, currency);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING,
      CommandArguments.DOUBLE, CommandArguments.STRING}, inputNames = {"Player",
      "Add, Rem", "Amount", "Currency"})
  public void changeDoubleCustom(ICommandSender sender, EntityPlayer player, String type,
      double amount, String currency) {
    if (type.equalsIgnoreCase("add")) {
      EcoUtils.addCurrency(player, currency, amount);
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).ECO_ADD
          .replaceAll("%PLAYER%", player.getDisplayNameString())
          .replaceAll("%AMOUNT%", "" + amount));
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).ECO_PAY_GIVEN
          .replaceAll("%PLAYER%", sender.getDisplayName().getUnformattedText())
          .replaceAll("%AMOUNT%", amount + ""));
    } else if (type.equalsIgnoreCase("remove") || type.equalsIgnoreCase("rem")) {
      EcoUtils.consumeCurrency(player, currency, amount);
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).ECO_DEL
          .replaceAll("%PLAYER%", player.getDisplayNameString())
          .replaceAll("%AMOUNT%", "" + amount));
    }
  }
}
