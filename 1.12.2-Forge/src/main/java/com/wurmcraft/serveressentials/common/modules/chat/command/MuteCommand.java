package com.wurmcraft.serveressentials.common.modules.chat.command;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;
import static com.wurmcraft.serveressentials.common.command.CommandUtils.convertToTime;
import static com.wurmcraft.serveressentials.common.command.CommandUtils.isUUID;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.RequestGenerator;
import java.time.Instant;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Chat", name = "Mute")
public class MuteCommand {

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.STRING_ARR},
      usage = {"player", "time"},
      canConsoleUse = true)
  public static void mute(ServerPlayer player, EntityPlayer muted, String[] inputs) {
    long muteTime = convertToTime(inputs);
    if (muteTime > 0) {
      SECore.dataLoader.delete(
          DataLoader.DataType.ACCOUNT, muted.getGameProfile().getId().toString(), true);
      Account account =
          SECore.dataLoader.get(
              DataLoader.DataType.ACCOUNT,
              muted.getGameProfile().getId().toString(),
              new Account());
      account.muted = !account.muted;
      if (account.muted) {
        account.mute_time = Instant.now().getEpochSecond() + muteTime;
        boolean updated =
            SECore.dataLoader.update(
                DataLoader.DataType.ACCOUNT, muted.getGameProfile().getId().toString(), account);
        if (updated) {
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_MUTE
                  .replaceAll("\\{@TIME@}", CommandUtils.displayTime(muteTime))
                  .replaceAll("\\{@PLAYER@}", muted.getDisplayNameString()));
          ChatHelper.send(
              muted,
              player.lang.COMMAND_MUTE_MUTED.replaceAll(
                  "\\{@TIME@}", CommandUtils.displayTime(muteTime)));
        } else {
          LOG.warn(
              "Failed to update user's mute ("
                  + player.player.getDisplayNameString()
                  + ") '"
                  + muted.getGameProfile().getId().toString()
                  + "'");
        }
      } else {
        account.mute_time = 0L;
        boolean updated =
            SECore.dataLoader.update(
                DataLoader.DataType.ACCOUNT, muted.getGameProfile().getId().toString(), account);
        if (updated) {
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_MUTE_UNDO
                  .replaceAll("\\{@TIME@}", CommandUtils.displayTime(muteTime))
                  .replaceAll("\\{@PLAYER@}", muted.getDisplayNameString()));
          ChatHelper.send(player.sender, player.lang.COMMAND_MUTE_MUTED_UNDO);
        } else
          LOG.warn(
              "Failed to update user's mute ("
                  + player.player.getDisplayNameString()
                  + ") '"
                  + muted.getGameProfile().getId().toString()
                  + "'");
      }
    } else ChatHelper.send(player.sender, player.lang.COMMAND_MUTE_LESS_THEN_0);
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"},
      canConsoleUse = true)
  public static void mute(ServerPlayer player, EntityPlayer muted) {
    mute(
        player,
        muted,
        ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultMuteDuration.split(" "));
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING_ARR},
      usage = {"playerUUID", "time"},
      canConsoleUse = true)
  public static void muteOffworld(ServerPlayer player, String uuid, String[] inputs) {
    if (SECore.dataLoader.getClass().isInstance(RestDataLoader.class)) {
      if (isUUID(uuid)) {
        muteRemote(player, uuid, inputs);
      } else { // Username?
        try {
          RequestGenerator.HttpResponse response = RequestGenerator.get("api/lookup/uuid/" + uuid);
          if (response.status == 200) {
            Account account = GSON.fromJson(response.response, Account.class);
            muteRemote(player, account.uuid, inputs);
          } else {
            // TODO Send Not Found Error
          }
        } catch (Exception e) {
          LOG.warn("Failed to get response from API, (" + e.getMessage() + ")");
        }
      }
    } else ChatHelper.send(player.sender, player.lang.DISABLED);
  }

  private static void muteRemote(ServerPlayer player, String uuid, String[] inputs) {
    Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, uuid, new Account());
    account.muted = !account.muted;
    long muteTime = convertToTime(inputs);
    account.mute_time = Instant.now().getEpochSecond() + muteTime;
    boolean updated = SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, uuid, account);
    if (account.muted) {
      if (updated) {
        ChatHelper.send(
            player.sender,
            player
                .lang
                .COMMAND_MUTE
                .replaceAll("\\{@TIME@}", CommandUtils.displayTime(muteTime))
                .replaceAll("\\{@PLAYER@}", uuid));
      } else LOG.warn("Failed to update user's mute (" + uuid + ")");
    } else {
      if (updated) {
        ChatHelper.send(
            player.sender,
            player
                .lang
                .COMMAND_MUTE_UNDO
                .replaceAll("\\{@TIME@}", CommandUtils.displayTime(muteTime))
                .replaceAll("\\{@PLAYER@}", uuid));
      } else LOG.warn("Failed to update user's mute (" + uuid + ")");
    }
  }
}
