package com.wurmcraft.serveressentials.forge.modules.economy.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "Balance", name = "Balance")
public class BalanceCommand {

  @Command(inputArguments = {})
  public void displayBal(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      StoredPlayer playerData = PlayerUtils.get(player);
      ChatHelper
          .sendSpacerWithMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER,
              "Balance");
      for (Currency currency : playerData.global.wallet.currency) {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(player).ECO_BAL.replaceAll("%NAME%", currency.name)
                .replaceAll("%AMOUNT%", "" + currency.amount));
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER})
  public void displayBalOther(ICommandSender sender, EntityPlayer player) {
    StoredPlayer playerData = PlayerUtils.get(player);
    ChatHelper
        .sendSpacerWithMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER,
            "Balance");
    for (Currency currency : playerData.global.wallet.currency) {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).ECO_BAL.replaceAll("%NAME%", currency.name)
              .replaceAll("%AMOUNT%", "" + currency.amount));
    }
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
  }
}
