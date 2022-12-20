package com.wurmcraft.serveressentials.common.modules.chat.command;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.HashMap;

@ModuleCommand(
    module = "Chat",
    name = "Channel",
    defaultAliases = {"ch", "chan"})
public class ChannelCommand {

  @Command(
      args = {},
      usage = {})
  public void defaultChannel(ServerPlayer player) {
    changeChannel(
        player,
        SECore.dataLoader.get(
            DataLoader.DataType.CHANNEL,
            ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel,
            new Channel()));
  }

  @Command(
      args = {CommandArgument.CHANNEL},
      usage = "ChannelName")
  public void changeChannel(ServerPlayer player, Channel ch) {
    player.local.channel = ch.name;
    if (SECore.dataLoader.update(
        DataLoader.DataType.LOCAL_ACCOUNT,
        player.player.getGameProfile().getId().toString(),
        player.local))
      ChatHelper.send(
          player.sender, player.lang.COMMAND_CHANNEL_CHANGED.replaceAll("\\{@CHANNEL@}", ch.name));
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"name"},
      isSubCommand = true,
      subCommandAliases = {"add", "c", "a"},
      canConsoleUse = true)
  public void create(ServerPlayer player, String name) {
    Channel ch = new Channel(name, "[" + name + "]", false, new HashMap<>(), true, "",true, new String[0]);
    if (SECore.dataLoader.register(DataLoader.DataType.CHANNEL, ch.name, ch)) {
      ChatHelper.send(
          player.sender, player.lang.COMMAND_CHANNEL_CREATED.replaceAll("\\{@NAME@}", name));
    } else
      ChatHelper.send(
          player.sender, player.lang.COMMAND_CHANNEL_EXISTS.replaceAll("\\{@NAME@}", name));
  }

  @Command(
      args = {CommandArgument.CHANNEL},
      usage = {"channel"},
      isSubCommand = true,
      subCommandAliases = {"del", "remove", "rem", "r", "d"},
      canConsoleUse = true)
  public void delete(ServerPlayer player, Channel ch) {
    // Prevent deletion of default channel
    String defaultChannel = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel;
    if (ch.name.equalsIgnoreCase(defaultChannel)) {
      ChatHelper.send(
          player.sender, player.lang.COMMAND_CHANNEL_DEFAULT.replaceAll("\\{@NAME@}", ch.name));
      return;
    }
    if (SECore.dataLoader.delete(DataLoader.DataType.CHANNEL, ch.name, false)) {
      ChatHelper.send(
          player.sender, player.lang.COMMAND_CHANNEL_DELETED.replaceAll("\\{@NAME@}", ch.name));
    } else LOG.warn("Failed to delete Channel '" + ch.name + "'");
  }

  @Command(
      args = {CommandArgument.CHANNEL},
      usage = {"channel"},
      isSubCommand = true,
      subCommandAliases = {"i", "information"},
      canConsoleUse = true)
  public void info(ServerPlayer player, Channel ch) {
    ChatHelper.send(player.sender, player.lang.SPACER);
    ChatHelper.send(
        player.sender, player.lang.COMMAND_CHANNEL_INFO_NAME.replaceAll("\\{@NAME@}", ch.name));
    ChatHelper.send(
        player.sender,
        player.lang.COMMAND_CHANNEL_INFO_PREFIX.replaceAll("\\{@PREFIX@}", ch.prefix));
    String loggingFormat = player.lang.COMMAND_CHANNEL_INFO_LOGGING;
    String replacement =
        player.lang.COMMAND_CHANNEL_INFO_LOGGING.substring(
            player.lang.COMMAND_CHANNEL_INFO_LOGGING.indexOf("{"),
            player.lang.COMMAND_CHANNEL_INFO_LOGGING.indexOf("}") + 1);
    String rep = replacement.substring(1, replacement.length() - 1).split(",")[ch.logChat ? 1 : 0];
    loggingFormat = loggingFormat.replace(replacement, rep);
    ChatHelper.send(player.sender, loggingFormat);
    ChatHelper.send(
        player.sender,
        player.lang.COMMAND_CHANNEL_INFO_FORMAT.replaceAll("\\{@FORMAT@}", ch.chatFormat));
    ChatHelper.send(player.sender, player.lang.SPACER);
  }

  @Command(
      args = {CommandArgument.CHANNEL, CommandArgument.STRING, CommandArgument.STRING},
      usage = {"channel", "prefix,logChat,enabled", "value"},
      isSubCommand = true,
      subCommandAliases = {"mod", "m"},
      canConsoleUse = true)
  public void modify(ServerPlayer player, Channel ch, String arg, String value) {
    if (arg.equalsIgnoreCase("prefix") || arg.equalsIgnoreCase("p")) {
      ch.prefix = value;
      if (SECore.dataLoader.update(DataLoader.DataType.CHANNEL, ch.name, ch))
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_CHANNEL_MODIFY_PREFIX.replaceAll("\\{@PREFIX@}", ch.prefix));
    } else if (arg.equalsIgnoreCase("logChat") || arg.equalsIgnoreCase("log")) {
      Boolean logChat = CommandUtils.convertBoolean(value);
      if (logChat != null) {
        ch.logChat = logChat;
        if (SECore.dataLoader.update(DataLoader.DataType.CHANNEL, ch.name, ch))
          ChatHelper.send(
              player.sender,
              player.lang.COMMAND_CHANNEL_MODIFY_LOG.replaceAll("\\{@VALUE@}", "" + logChat));
      } else ChatHelper.send(player.sender, player.lang.INVALID_BOOLEAN);
    } else if (arg.equalsIgnoreCase("enabled") || arg.equalsIgnoreCase("e")) {
      Boolean enabled = CommandUtils.convertBoolean(value);
      if (enabled != null) {
        ch.enabled = enabled;
        if (SECore.dataLoader.update(DataLoader.DataType.CHANNEL, ch.name, ch))
          ChatHelper.send(
              player.sender,
              player.lang.COMMAND_CHANNEL_MODIFY_ENABLED.replaceAll("\\{@VALUE@}", "" + enabled));
      } else ChatHelper.send(player.sender, player.lang.INVALID_BOOLEAN);
    }
  }
}
