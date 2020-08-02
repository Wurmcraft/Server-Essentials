package com.wurmcraft.serveressentials.forge.modules.economy.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "Economy", name = "Balance", aliases = {"B", "Money", "M",
    "Bal"})
public class BalanceCommand {

  @Command(inputArguments = {})
  public void displayBalance(ICommandSender sender) {
    if (sender != null && sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      StoredPlayer playerData = PlayerUtils.get(player);
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
      for (Currency c : playerData.global.wallet.currency) {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).ECO_BAL.replaceAll("%NAME%", c.name)
                .replaceAll("%AMOUNT%", "" + c.amount));
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void displayBalance(ICommandSender sender, EntityPlayer player) {
    if (RankUtils.hasPermission(sender, "economy.balance.other")) {
      if (sender != null && sender.getCommandSenderEntity() instanceof EntityPlayer) {
        StoredPlayer playerData = PlayerUtils.get(player);
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
        for (Currency c : playerData.global.wallet.currency) {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).ECO_BAL.replaceAll("%NAME%", c.name)
                  .replaceAll("%AMOUNT%", "" + c.amount));
        }
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper
          .sendHoverMessage(player, noPerms,
              TextFormatting.RED + "economy.balance.other");
    }
  }
}
