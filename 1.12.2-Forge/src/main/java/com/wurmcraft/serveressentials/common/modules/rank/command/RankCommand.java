package com.wurmcraft.serveressentials.common.modules.rank.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import joptsimple.internal.Strings;

@ModuleCommand(module = "Rank", name = "Rank")
public class RankCommand {

  @Command(args = {CommandArgument.STRING}, usage = {
      "name"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {"c", "add",
      "a"})
  public static void create(ServerPlayer player, String name) {
    Rank rank = new Rank(name, new String[]{}, new String[]{}, "[" + name + "]", 0, "", 0,
        "", 0);
    if (SECore.dataLoader.get(DataType.RANK, name) == null) {
      SECore.dataLoader.register(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_CREATED.replaceAll("\\{@NAME@}", name));
      ServerEssentials.LOG.info("Rank " + name + " created!");
    } else {
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_EXISTS.replaceAll("\\{@NAME@}", name));
    }
  }

  @Command(args = {CommandArgument.RANK}, usage = {
      "rank"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {"del", "d",
      "remove", "rem", "r"})
  public static void delete(ServerPlayer player, Rank rank) {
    SECore.dataLoader.delete(DataType.RANK, rank.name);
    ChatHelper.send(player.sender, player.lang.COMMAND_RANK_DELETED);
    ServerEssentials.LOG.info("Rank " + rank.name + " deleted!");
  }

  @Command(args = {CommandArgument.RANK}, usage = {
      "rank"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {
      "information", "i"})
  public static void info(ServerPlayer player, Rank rank) {
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_NAME.replaceAll("\\{@NAME@}", rank.name));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_PERMISSIONS.replaceAll("\\{@PERMS@}",
            Strings.join(rank.permissions, ", ")));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_INHERITANCE.replaceAll("\\{@INHERITANCE@}",
            Strings.join(rank.inheritance, ", ")));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_PREFIX.replaceAll("\\{@PREFIX@}", rank.prefix));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_SUFFIX.replaceAll("\\{@SUFFIX@}", rank.suffix));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_PREFIX_PRIORITY.replaceAll("\\{@NUM@}",
            rank.prefix_priority + ""));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_SUFFIX_PRIORITY.replaceAll("\\{@NUM@}",
            rank.suffix_priority + ""));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_COLOR.replaceAll("\\{@COLOR@}", rank.color));
    ChatHelper.send(player.sender,
        player.lang.COMMAND_RANK_INFO_COLOR_PRIORITY.replaceAll("\\{@NUM@}",
            rank.color_priority + ""));
  }


  @Command(args = {CommandArgument.RANK, CommandArgument.STRING,
      CommandArgument.STRING, CommandArgument.STRING}, usage = {"rank", "add, remove",
      "perm, inheritance, prefix, suffix, prefix_priority, suffix_priority, color, color_priority",
      "data"}, isSubCommand = true, canConsoleUse = true)
  public void modify(ServerPlayer player, Rank rank, String action, String type,
      String data) {
    if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("a")) {
      add(player, rank, type, data);
    } else if (action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("del")
        || action.equalsIgnoreCase("d") || action.equalsIgnoreCase("remove")
        || action.equalsIgnoreCase("rem") || action.equalsIgnoreCase("r")) {
      remove(player, rank, type, data);
    }
  }

  public static void add(ServerPlayer player, Rank rank, String type, String data) {
    if (type.equalsIgnoreCase("perm") || type.equals("perms") || type.equalsIgnoreCase(
        "permission") || type.equalsIgnoreCase("permissions")) {
      List<String> perms = new ArrayList<>();
      Collections.addAll(perms, rank.permissions);
      perms.add(data);
      rank.permissions = perms.toArray(new String[0]);
      SECore.dataLoader.update(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_ADD_PERMS.replaceAll("\\{@NAME@}", rank.name)
              .replaceAll("\\{@PERM@}", data));
      ServerEssentials.LOG.info("Perm '" + data + "' added to rank '" + rank.name);
    } else if (type.equalsIgnoreCase("inheritance") || type.equalsIgnoreCase("inher")) {
      List<String> inheratance = new ArrayList<>();
      Collections.addAll(inheratance, rank.inheritance);
      inheratance.add(data);
      rank.inheritance = inheratance.toArray(new String[0]);
      SECore.dataLoader.update(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_ADD_INHERITANCE.replaceAll("\\{@NAME@}", rank.name)
              .replaceAll("\\{@INHERITANCE@}", data));
      ServerEssentials.LOG.info("Inheritance '" + data + "' added to rank '" + rank.name);
    } else if (type.equalsIgnoreCase("prefix")) {
      rank.prefix = data;
      SECore.dataLoader.update(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_ADD_PREFIX.replaceAll("\\{@NAME@}", rank.name)
              .replaceAll("\\{@PREFIX@}", data));
      ServerEssentials.LOG.info("Prefix '" + data + "' changed for rank '" + rank.name);
    } else if (type.equalsIgnoreCase("suffix")) {
      rank.suffix = data;
      SECore.dataLoader.update(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_ADD_SUFFIX.replaceAll("\\{@NAME@}", rank.name)
              .replaceAll("\\{@SUFFIX@}", data));
      ServerEssentials.LOG.info("Suffix '" + data + "' changed for rank '" + rank.name);
    } else if (type.equalsIgnoreCase("prefix_priority")) {
      if (CommandUtils.isNumber(data)) {
        rank.prefix_priority = (int) CommandUtils.number(data);
        SECore.dataLoader.update(DataType.RANK, rank.name, rank);
        ChatHelper.send(player.sender,
            player.lang.COMMAND_RANK_ADD_PREFIX_PRIORITY.replaceAll("\\{@NAME@}",
                    rank.name)
                .replaceAll("\\{@NUM@}", data));
        ServerEssentials.LOG.info(
            "Prefix Priority '" + data + "' has been changed for rank '" + rank.name);
      } else {
        ChatHelper.send(player.sender,
            player.lang.NUMBER_REQUIRED.replaceAll("\\{@NUM@}", data));
      }
    } else if (type.equalsIgnoreCase("suffix_priority")) {
      if (CommandUtils.isNumber(data)) {
        rank.suffix_priority = (int) CommandUtils.number(data);
        SECore.dataLoader.update(DataType.RANK, rank.name, rank);
        ChatHelper.send(player.sender,
            player.lang.COMMAND_RANK_ADD_SUFFIX_PRIORITY.replaceAll("\\{@NAME@}",
                    rank.name)
                .replaceAll("\\{@NUM@}", data));
        ServerEssentials.LOG.info(
            "Suffix Priority '" + data + "' has been changed for rank '" + rank.name);
      } else {
        ChatHelper.send(player.sender,
            player.lang.NUMBER_REQUIRED.replaceAll("\\{@NUM@}", data));
      }
    } else if (type.equalsIgnoreCase("color_priority")) {
      if (CommandUtils.isNumber(data)) {
        rank.color_priority = (int) CommandUtils.number(data);
        SECore.dataLoader.update(DataType.RANK, rank.name, rank);
        ChatHelper.send(player.sender,
            player.lang.COMMAND_RANK_ADD_COLOR_PRIORITY.replaceAll("\\{@NAME@}",
                    rank.name)
                .replaceAll("\\{@NUM@}", data));
        ServerEssentials.LOG.info(
            "Color Priority '" + data + "' has been changed for rank '" + rank.name);
      } else {
        ChatHelper.send(player.sender,
            player.lang.NUMBER_REQUIRED.replaceAll("\\{@NUM@}", data));
      }
    } else if (type.equalsIgnoreCase("color")) {
      rank.color = data;
      SECore.dataLoader.update(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_ADD_COLOR.replaceAll("\\{@NAME@}", rank.name)
              .replaceAll("\\{@COLOR@}", data));
      ServerEssentials.LOG.info(
          "Color '" + data + "' has been set for rank '" + rank.name);
    }
  }

  public static void remove(ServerPlayer player, Rank rank, String type, String data) {
    if (type.equalsIgnoreCase("perm") || type.equals("perms") || type.equalsIgnoreCase(
        "permission") || type.equalsIgnoreCase("permissions")) {
      List<String> perms = new ArrayList<>();
      Collections.addAll(perms, rank.permissions);
      perms.add(data);
      rank.permissions = perms.toArray(new String[0]);
      SECore.dataLoader.update(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_ADD_PERMS.replaceAll("\\{@NAME@}", rank.name)
              .replaceAll("\\{@PERM@}", data));
      ServerEssentials.LOG.info("Perm '" + data + "' removed from rank '" + rank.name);
    } else if (type.equalsIgnoreCase("inheritance") || type.equalsIgnoreCase("inher")) {
      List<String> inheratance = new ArrayList<>();
      Collections.addAll(inheratance, rank.inheritance);
      inheratance.remove(data);
      rank.inheritance = inheratance.toArray(new String[0]);
      SECore.dataLoader.update(DataType.RANK, rank.name, rank);
      ChatHelper.send(player.sender,
          player.lang.COMMAND_RANK_ADD_INHERITANCE.replaceAll("\\{@NAME@}", rank.name)
              .replaceAll("\\{@INHERITANCE@}", data));
      ServerEssentials.LOG.info(
          "Inheritance '" + data + "' removed from rank '" + rank.name);
    }
  }
}
