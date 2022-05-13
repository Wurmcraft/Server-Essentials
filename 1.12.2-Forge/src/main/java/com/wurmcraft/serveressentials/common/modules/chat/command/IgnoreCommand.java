package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(
    module = "Chat",
    name = "Ignore",
    defaultAliases = {"I"})
public class IgnoreCommand {

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void ignorePlayer(ServerPlayer player, EntityPlayer otherPlayer) {
    if (otherPlayer
        .getGameProfile()
        .getId()
        .toString()
        .equals(player.player.getGameProfile().getId().toString())) {
      ChatHelper.send(player.sender, player.lang.COMMAND_IGNORE_SELF);
      return;
    }
    List<String> existing =
        new ArrayList<>(
            Arrays.asList(
                player.local.ignoredUsers != null ? player.local.ignoredUsers : new String[0]));
    // Ignoring User
    if (!existing.contains(otherPlayer.getGameProfile().getId().toString())) {
      existing.add(otherPlayer.getGameProfile().getId().toString());
      player.local.ignoredUsers = existing.toArray(new String[0]);
      SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, player.local.uuid, player.local);
      ChatHelper.send(
          player.player,
          player.lang.COMMAND_IGNORE_IGNORED.replaceAll(
              "\\{@USERNAME@}", otherPlayer.getDisplayNameString()));
    } else { // Un-ignoring user
      existing.remove(otherPlayer.getGameProfile().getId().toString());
      player.local.ignoredUsers = existing.toArray(new String[0]);
      SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, player.local.uuid, player.local);
      ChatHelper.send(
          player.player,
          player.lang.COMMAND_IGNORE_UNDO.replaceAll(
              "\\{@USERNAME@}", otherPlayer.getDisplayNameString()));
    }
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"player"})
  public void ignorePlayer(ServerPlayer player, String otherPlayer) {
    if (player.player.getGameProfile().getId().toString().equalsIgnoreCase(otherPlayer)
        || player.player.getDisplayNameString().equalsIgnoreCase(otherPlayer)) {
      ChatHelper.send(player.sender, player.lang.COMMAND_IGNORE_SELF);
      return;
    }
    String uuid = PlayerUtils.getUUIDForInput(otherPlayer);
    if (uuid != null) {
      List<String> existing =
          new ArrayList<>(
              Arrays.asList(
                  player.local.ignoredUsers != null ? player.local.ignoredUsers : new String[0]));
      // Ignoring User
      if (!existing.contains(uuid)) {
        existing.add(uuid);
        player.local.ignoredUsers = existing.toArray(new String[0]);
        SECore.dataLoader.update(
            DataLoader.DataType.LOCAL_ACCOUNT, player.local.uuid, player.local);
        ChatHelper.send(
            player.player,
            player.lang.COMMAND_IGNORE_IGNORED.replaceAll(
                "\\{@USERNAME@}", PlayerUtils.getUsernameForInput(uuid)));
      } else { // Un-ignoring user
        existing.remove(uuid);
        player.local.ignoredUsers = existing.toArray(new String[0]);
        SECore.dataLoader.update(
            DataLoader.DataType.LOCAL_ACCOUNT, player.local.uuid, player.local);
        ChatHelper.send(
            player.player,
            player.lang.COMMAND_IGNORE_UNDO.replaceAll(
                "\\{@USERNAME@}", PlayerUtils.getUsernameForInput(uuid)));
      }
    } else
      ChatHelper.send(
          player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", otherPlayer));
  }
}
