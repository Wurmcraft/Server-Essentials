package com.wurmcraft.serveressentials.forge.modules.economy.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import com.wurmcraft.serveressentials.forge.modules.economy.utils.EcoUtils;
import com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "Economy", name = "Perk")
public class PerkCommand {

  public static final String[] PERKS = new String[]{"Home", "ClaimBlocks"};

  @Command(inputArguments = {})
  public void listPerks(ICommandSender sender) {
    listPerks(sender, "list");
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"list"})
  public void listPerks(ICommandSender sender, String args) {
    if (args.equalsIgnoreCase("list")) {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
      for (String perk : PERKS) {
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + perk);
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
    }
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.STRING}, inputNames = {"buy", "Home, ClaimBlocks"})
  public void buyPerk(ICommandSender sender, String arg, String perk) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (arg.equalsIgnoreCase("buy")) {
        if (perk.equalsIgnoreCase("home")) {
          int currentLevel = GeneralUtils.getPerkLevel(player, "Home");
          double cost = 0;
          if (currentLevel > 0) {
            cost = (currentLevel * EconomyModule.config.homeCostMultier
                * EconomyModule.config.homeCostMultier);
          } else {
            cost = (currentLevel * EconomyModule.config.homeCostMultier);
          }
          if (EcoUtils.hasCurrency(player, cost)) {
            EcoUtils.consumeCurrency(player, cost);
            GeneralUtils.setPerk(player, "home", currentLevel + 1);
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).ECO_PERK_BUY.replaceAll("%NAME%", "Home")
                    .replaceAll("%AMOUNT%", "" + cost));
          } else {
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).ECO_MONEY_INSUFFICENT
                    .replaceAll("%AMOUNT%", "" + cost));
          }
        } else if (perk.equalsIgnoreCase("Claim") || perk
            .equalsIgnoreCase("ClaimBlocks")) {
          int currentLevel = GeneralUtils.getPerkLevel(player, "claimblocks");
          double cost = currentLevel * EconomyModule.config.claimBlockCost;
          if (EcoUtils.hasCurrency(player, cost)) {
            EcoUtils.consumeCurrency(player, cost);
            GeneralUtils.setPerk(player, "claimblocks", currentLevel + 1);
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).ECO_PERK_BUY.replaceAll("%NAME%", "Home")
                    .replaceAll("%AMOUNT%", "" + cost));
          } else {
            ChatHelper.sendMessage(sender,
                PlayerUtils.getLanguage(sender).ECO_MONEY_INSUFFICENT
                    .replaceAll("%AMOUNT%", "" + cost));
          }
        } else {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).ECO_PERK_NONE.replaceAll("%NAME%", perk));
        }
      }
    }
  }

}
