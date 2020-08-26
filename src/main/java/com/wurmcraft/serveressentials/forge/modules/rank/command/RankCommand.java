package com.wurmcraft.serveressentials.forge.modules.rank.command;


import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.command.SECommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "Rank", name = "Rank")
public class RankCommand {

  @Command(inputArguments = {CommandArguments.PLAYER,
      CommandArguments.RANK}, inputNames = {"Player", "Rank"})
  public void changeUserRank(ICommandSender sender, EntityPlayer player, Rank rank) {
    StoredPlayer playerData = PlayerUtils.get(player);
    playerData.global.rank = rank.getID();
    RankUtils.changeRank(player, rank);
    ChatHelper.sendMessage(player,
        PlayerUtils.getLanguage(player).RANK_CHANGE.replaceAll("%RANK%", rank.name));
    if (!(sender instanceof EntityPlayer) || !((EntityPlayer) sender
        .getCommandSenderEntity()).getGameProfile().getId()
        .equals(player.getGameProfile().getId())) {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).RANK_CHANGE_SENDER
          .replaceAll("%PLAYER%", player.getDisplayNameString())
          .replaceAll("%RANK%", rank.name));
    }
  }

  @Command(inputArguments = {CommandArguments.RANK, CommandArguments.STRING,
      CommandArguments.STRING}, inputNames = {"Rank", "prefix,suffix", "name"})
  public void changePrefixSuffix(ICommandSender sender, Rank rank, String arg,
      String name) {
    if (RankUtils.hasPermission(sender, "rank.change")) {
      if (arg.equalsIgnoreCase("prefix") || arg.equalsIgnoreCase("pre") || arg
          .equalsIgnoreCase("p")) {
        rank.prefix = arg;
        RankUtils.updateRank(rank);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).RANK_PREFIX.replaceAll("%RANK%", rank.name)
                .replaceAll("%PREFIX%", rank.prefix));
      } else if (arg.equalsIgnoreCase("suffix") || arg.equalsIgnoreCase("after") || arg
          .equalsIgnoreCase("suff") || arg.equalsIgnoreCase("s")) {
        rank.suffix = arg;
        RankUtils.updateRank(rank);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).RANK_SUFFIX.replaceAll("%RANK%", rank.name)
                .replaceAll("%SUFFIX%", rank.suffix));
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%",
                    "<rank> <permission, inheritance> <add,rem> <node>"));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%", "<player> <rank>"));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%", "<create, delete> <rank>"));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms, TextFormatting.RED + "rank.change");
    }
  }

  @Command(inputArguments = {CommandArguments.RANK, CommandArguments.STRING,
      CommandArguments.STRING, CommandArguments.STRING}, inputNames = {"Rank",})
  public void changePerm(ICommandSender sender, Rank rank, String type, String arg,
      String node) {
    if (RankUtils.hasPermission(sender, "rank.change")) {
      boolean isRemove =
          arg.equalsIgnoreCase("isRemove") || arg.equalsIgnoreCase("rem") || arg
              .equalsIgnoreCase("r") || arg.equalsIgnoreCase("delete") || arg
              .equalsIgnoreCase("del") || arg.equalsIgnoreCase("d");
      if (type.equalsIgnoreCase("permission") || type.equalsIgnoreCase("perm") || type
          .equalsIgnoreCase("p")) {
        if (arg.equalsIgnoreCase("add") || arg.equalsIgnoreCase("a")) {
          List<String> currentPerms = new ArrayList<>();
          Collections.addAll(currentPerms, rank.permission);
          currentPerms.add(node);
          rank.permission = currentPerms.toArray(new String[0]);
          RankUtils.updateRank(rank);
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).RANK_PERM_ADD.replaceAll("%NODE%", node)
                  .replaceAll("%RANK%", rank.name));
        } else if (isRemove) {
          List<String> currentPerms = new ArrayList<>();
          Collections.addAll(currentPerms, rank.permission);
          currentPerms.remove(node);
          rank.permission = currentPerms.toArray(new String[0]);
          RankUtils.updateRank(rank);
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).RANK_PERM_DEL.replaceAll("%NODE%", node)
                  .replaceAll("%RANK%", rank.name));
        } else {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).COMMAND_USAGE
                  .replaceAll("%COMMAND%", "Rank")
                  .replaceAll("%ARGS%",
                      "<rank> <permission, inheritance> <add,rem> <node>"));
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).COMMAND_USAGE
                  .replaceAll("%COMMAND%", "Rank")
                  .replaceAll("%ARGS%", "<player> <rank>"));
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).COMMAND_USAGE
                  .replaceAll("%COMMAND%", "Rank")
                  .replaceAll("%ARGS%", "<create, delete> <rank>"));
        }
      } else if (type.equalsIgnoreCase("inheritance") || type.equalsIgnoreCase("inh")
          || type.equalsIgnoreCase("i")) {
        if (arg.equalsIgnoreCase("add") || arg.equalsIgnoreCase("a")) {
          List<String> currentInherit = new ArrayList<>();
          Collections.addAll(currentInherit, rank.inheritance);
          currentInherit.add(node);
          rank.inheritance = currentInherit.toArray(new String[0]);
          RankUtils.updateRank(rank);
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).RANK_INHERIT_ADD.replaceAll("%NODE%", node)
                  .replaceAll("%RANK%", rank.name));
        } else if (isRemove) {
          List<String> currentInherit = new ArrayList<>();
          Collections.addAll(currentInherit, rank.inheritance);
          currentInherit.remove(node);
          rank.inheritance = currentInherit.toArray(new String[0]);
          RankUtils.updateRank(rank);
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).RANK_INHERIT_DEL.replaceAll("%NODE%", node)
                  .replaceAll("%RANK%", rank.name));
        } else {
          ChatHelper.sendMessage(sender,
              PlayerUtils.getLanguage(sender).COMMAND_USAGE
                  .replaceAll("%COMMAND%", "Rank")
                  .replaceAll("%ARGS%",
                      "<rank> <permission, inheritance> <add,rem> <node>"));
        }
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%",
                    "<rank> <permission, inheritance> <add,rem> <node>"));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%", "<player> <rank>"));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%", "<create, delete> <rank>"));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms, TextFormatting.RED + "rank.change");
    }
  }

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"List"})
  public void listRanks(ICommandSender sender, String arg) {
    if (arg.equalsIgnoreCase("list")) {
      ChatHelper
          .sendSpacerWithMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER,
              "Ranks");
      for (Rank rank : SECore.dataHandler.getDataFromKey(DataKey.RANK, new Rank())
          .values()) {
        ChatHelper
            .sendMessage(sender, rank.prefix + rank.suffix + " (" + rank.name + ")");
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
    } else {
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
              .replaceAll("%ARGS%", "<rank> <permission, inheritance> <add,rem> <node>"));
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
              .replaceAll("%ARGS%", "<player> <rank>"));
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
              .replaceAll("%ARGS%", "<create, delete> <rank>"));
    }
  }

  @Command(inputArguments = {CommandArguments.STRING, CommandArguments.STRING})
  public void createOrDelete(ICommandSender sender, String arg, String rank) {
    if (RankUtils.hasPermission(sender, "rank.change")) {
      if (arg.equalsIgnoreCase("create") || arg.equalsIgnoreCase("c") || arg
          .equalsIgnoreCase("add") || arg.equalsIgnoreCase("a")) {
        Rank newRank = new Rank(rank, "[" + rank + "]", "", new String[]{},
            new String[]{});
        SECore.dataHandler.registerData(DataKey.RANK, newRank);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).RANK_CREATE
                .replaceAll("%RANK%", newRank.name));
      } else if (arg.equalsIgnoreCase("delete") || arg.equalsIgnoreCase("del") || arg
          .equalsIgnoreCase("d") || arg.equalsIgnoreCase("remove") || arg
          .equalsIgnoreCase("rem") || arg.equalsIgnoreCase("r")) {
        SECore.dataHandler.delData(DataKey.RANK, rank, true);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).RANK_DELETE.replaceAll("%RANK%", rank));
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%",
                    "<rank> <permission, inheritance> <add,rem> <node>"));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%", "<player> <rank>"));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Rank")
                .replaceAll("%ARGS%", "<create, delete> <rank>"));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms, TextFormatting.RED + "rank.change");
    }
  }
}
