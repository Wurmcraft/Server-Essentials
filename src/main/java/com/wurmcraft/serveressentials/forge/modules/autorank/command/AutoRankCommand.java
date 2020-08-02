package com.wurmcraft.serveressentials.forge.modules.autorank.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.modules.autorank.utils.AutoRankUtils;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyConfig;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.data.Language;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.NoSuchElementException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.time.DurationFormatUtils;

@ModuleCommand(moduleName = "AutoRank", name = "Ar", aliases = {"AutoRank"})
public class AutoRankCommand {

  @Command(inputArguments = {})
  public void arBasic(ICommandSender sender) {
    check(sender, "check");
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.PLAYER}, inputNames = {"check", "player"})
  public void check(ICommandSender sender, String arg, EntityPlayer player) {
    if (arg.equalsIgnoreCase("check")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        Language senderLanguage = PlayerUtils.getLanguage(sender);
        if (RankUtils.hasPermission(sender, "autorank.check.other")) {
          try {
            AutoRank ar = AutoRankUtils.getNextUpdatePath(player);
            if (ar == null) {
              throw new NoSuchElementException();
            }
            Rank nextRank = (Rank) SECore.dataHandler.getData(DataKey.RANK, ar.nextRank);
            ChatHelper
                .sendSpacerWithMessage(sender, senderLanguage.COMMAND_SPACER, "AutoRank");
            ChatHelper.sendHoverMessage(sender,
                senderLanguage.AUTORANK_AR_NEXT.replaceAll("%RANK%", nextRank.name),
                nextRank.prefix + " " + player.getDisplayNameString());
            long timeLeft = ar.playTime - PlayerUtils.getTotalPlayTime(player);
            if (timeLeft < 0) {
              timeLeft = 0;
            }
            ChatHelper.sendMessage(sender, senderLanguage.AUTORANK_AR_TIME
                .replaceAll("%TIME%",
                    DurationFormatUtils.formatDuration(timeLeft * 60000, "d%:H$:m#:s@")
                        .replace('%', 'D')
                        .replace('$', 'H').replace('#', 'M').replace('@', 'S')
                        .replaceAll(":", ", ")));
            if (ar.exp > 0) {
              ChatHelper.sendMessage(sender,
                  senderLanguage.AUTORANK_AR_XP.replaceAll("%XP%", ar.exp + ""));
            }
            if (ar.balance > 0) {
              ChatHelper.sendMessage(sender,
                  senderLanguage.AUTORANK_AR_MONEY.replaceAll("%AMOUNT%", ar.balance + "")
                      .replaceAll("%NAME%", EconomyModule.config.defaultCurrency.name));
            }
            ChatHelper.sendMessage(sender, senderLanguage.COMMAND_SPACER);
            AutoRankUtils.checkAndHandleRankup(player);
          } catch (NoSuchElementException e) {
            ChatHelper.sendMessage(sender, senderLanguage.AUTORANK_AR_MAX);
          }
        } else {
          TextComponentTranslation noPerms = new TextComponentTranslation(
              "commands.generic.permission", new Object[0]);
          noPerms.getStyle().setColor(TextFormatting.RED);
          ChatHelper.sendHoverMessage(sender, noPerms,
              TextFormatting.RED + "autorank.check.other");
        }
      }
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "AR")
              .replaceAll("%ARGS%", "check, create, remove"));
    }
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"check, reload"})
  public void check(ICommandSender sender, String arg) {
    if (arg.equalsIgnoreCase("check")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        Language senderLanguage = PlayerUtils.getLanguage(player);
        if (RankUtils.hasPermission(player, "autorank.check")) {
          try {
            AutoRank ar = AutoRankUtils.getNextUpdatePath(player);
            if (ar == null) {
              throw new NoSuchElementException();
            }
            Rank nextRank = (Rank) SECore.dataHandler.getData(DataKey.RANK, ar.nextRank);
            ChatHelper
                .sendSpacerWithMessage(player, senderLanguage.COMMAND_SPACER, "AutoRank");
            ChatHelper.sendHoverMessage(player,
                senderLanguage.AUTORANK_AR_NEXT.replaceAll("%RANK%", nextRank.name),
                nextRank.prefix + " " + player.getDisplayNameString());
            long timeLeft = ar.playTime - PlayerUtils.getTotalPlayTime(player);
            if (timeLeft < 0) {
              timeLeft = 0;
            }
            ChatHelper.sendMessage(player, senderLanguage.AUTORANK_AR_TIME
                .replaceAll("%TIME%",
                    DurationFormatUtils.formatDuration(timeLeft * 60000, "d%:H$:m#:s@")
                        .replace('%', 'D')
                        .replace('$', 'H').replace('#', 'M').replace('@', 'S')
                        .replaceAll(":", ", ")));
            if (ar.exp > 0) {
              ChatHelper.sendMessage(player,
                  senderLanguage.AUTORANK_AR_XP.replaceAll("%XP%", ar.exp + ""));
            }
            if (ar.balance > 0) {
              ChatHelper.sendMessage(player,
                  senderLanguage.AUTORANK_AR_MONEY.replaceAll("%AMOUNT%", ar.balance + "")
                      .replaceAll("%NAME%", EconomyModule.config.defaultCurrency.name));
            }
            ChatHelper.sendMessage(player, senderLanguage.COMMAND_SPACER);
            AutoRankUtils.checkAndHandleRankup(player);
          } catch (NoSuchElementException e) {
            ChatHelper.sendMessage(sender, senderLanguage.AUTORANK_AR_MAX);
          }
        } else {
          TextComponentTranslation noPerms = new TextComponentTranslation(
              "commands.generic.permission", new Object[0]);
          noPerms.getStyle().setColor(TextFormatting.RED);
          ChatHelper
              .sendHoverMessage(sender, noPerms, TextFormatting.RED + "autorank.check");
        }
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "AR")
                .replaceAll("%ARGS%", "check, create, remove"));
      }
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "AR")
              .replaceAll("%ARGS%", "check, create, remove"));
    }
  }

  @Command(inputArguments = {CommandArguments.STRING, CommandArguments.RANK,
      CommandArguments.RANK, CommandArguments.INTEGER}, inputNames = {"create",
      "currentRank", "nextRank", "timeInMinutes"})
  public void createTimeOnly(ICommandSender sender, String arg, Rank currentRank,
      Rank nextRank, int time) {
    createFull(sender, arg, currentRank, nextRank, time, 0, 0);
  }

  @Command(inputArguments = {CommandArguments.STRING, CommandArguments.RANK,
      CommandArguments.RANK, CommandArguments.INTEGER, CommandArguments.INTEGER,
      CommandArguments.INTEGER}, inputNames = {"create", "currentRank",
      "nextRank", "timeInMinutes", "requiredCurrencyAmount", "expLvl's"})
  public void createFull(ICommandSender sender, String arg, Rank currentRank,
      Rank nextRank, int time, int balance, int xp) {
    Language senderLanguage = PlayerUtils.getLanguage(sender);
    if (arg.equalsIgnoreCase("create") || arg.equalsIgnoreCase("add")) {
      if (RankUtils.hasPermission(sender, "autorank.create")) {
        AutoRank autoRank = new AutoRank(currentRank.getID(), nextRank.getID(), time,
            balance, xp);
        SECore.dataHandler.registerData(DataKey.AUTO_RANK, autoRank);
        ChatHelper.sendMessage(sender,
            senderLanguage.AUTORANK_AR_CREATE.replaceAll("%NAME%", autoRank.rank));
        ChatHelper
            .sendSpacerWithMessage(sender, senderLanguage.COMMAND_SPACER, "AutoRank");
        ChatHelper.sendMessage(sender, senderLanguage.AUTORANK_AR_TIME
            .replaceAll("%TIME%",
                DurationFormatUtils
                    .formatDuration(autoRank.playTime * 60000, "d%:H$:m#:s@")
                    .replace('%', 'D')
                    .replace('$', 'H').replace('#', 'M').replace('@', 'S')
                    .replaceAll(":", ", ")));
        if (autoRank.exp > 0) {
          ChatHelper.sendMessage(sender,
              senderLanguage.AUTORANK_AR_XP.replaceAll("%XP%", autoRank.exp + ""));
        }
//        if (autoRank.balance > 0) {
//          ChatHelper.sendMessage(sender,
//              senderLanguage.AUTORANK_AR_MONEY
//                  .replaceAll("%AMOUNT%", autoRank.balance + "")
//                  .replaceAll("%NAME%", ((EconomyConfig) SERegistry
//                      .getStoredData(DataKey.MODULE_CONFIG,
//                          "Economy")).defaultServerCurrency.name));
//        }
        ChatHelper.sendMessage(sender, senderLanguage.COMMAND_SPACER);
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper
            .sendHoverMessage(sender, noPerms, TextFormatting.RED + "autorank.create");
      }
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "AR")
              .replaceAll("%ARGS%", "check, create, remove"));
    }
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.RANK}, inputNames = {"delete", "rank"})
  public void delete(ICommandSender sender, String arg, Rank rank) {
    if (arg.equalsIgnoreCase("delete") || arg.equalsIgnoreCase("del")
        || arg.equalsIgnoreCase("remove") && arg.equalsIgnoreCase("rem")) {
      Language senderLanguage = PlayerUtils.getLanguage(sender);
      if (RankUtils.hasPermission(sender, "autorank.delete")) {
        try {
          SECore.dataHandler.delData(DataKey.AUTO_RANK, rank.getID(), true);
          ChatHelper.sendMessage(sender,
              senderLanguage.AUTORANK_AR_DELETE.replaceAll("%NAME%", rank.name));
        } catch (NoSuchElementException e) {
        }
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper
            .sendHoverMessage(sender, noPerms, TextFormatting.RED + "autorank.delete");
      }
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "AR")
              .replaceAll("%ARGS%", "check, create, remove"));
    }
  }
}
