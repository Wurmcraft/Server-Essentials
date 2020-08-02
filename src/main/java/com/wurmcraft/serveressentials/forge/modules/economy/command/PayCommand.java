package com.wurmcraft.serveressentials.forge.modules.economy.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "Economy", name = "Pay", aliases = {"SendMoney", "PayPal"})
public class PayCommand {

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.DOUBLE})
  public void sendMoney(ICommandSender sender, EntityPlayer otherPlayer, double amount) {
    sendMoney(sender, otherPlayer, amount, EconomyModule.config.defaultCurrency.name);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.INTEGER})
  public void sendMoney(ICommandSender sender, EntityPlayer otherPlayer, int amount) {
    sendMoney(sender, otherPlayer, amount, EconomyModule.config.defaultCurrency.name);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.DOUBLE,
      CommandArguments.STRING})
  public void sendMoney(ICommandSender sender, EntityPlayer otherPlayer, double amount,
      String name) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (EcoUtils.hasCurrency(player, name, amount)) {
        EcoUtils.consumeCurrency(player, name, amount);
        EcoUtils.addCurrency(otherPlayer, name, amount);
        ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).ECO_PAY_SENT
            .replaceAll("%AMOUNT%", "" + amount)
            .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
        ChatHelper.sendMessage(otherPlayer,
            PlayerUtils.getLanguage(otherPlayer).ECO_PAY_GIVEN
                .replaceAll("%PLAYER%", player.getDisplayNameString())
                .replaceAll("%AMOUNT%", amount + ""));
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).ECO_MONEY_INSUFFICENT
                .replaceAll("%AMOUNT%", "" + amount));
      }
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.INTEGER,
      CommandArguments.STRING})
  public void sendMoney(ICommandSender sender, EntityPlayer otherPlayer, int amount,
      String name) {
    sendMoney(sender, otherPlayer, (double) amount, name);
  }

}
