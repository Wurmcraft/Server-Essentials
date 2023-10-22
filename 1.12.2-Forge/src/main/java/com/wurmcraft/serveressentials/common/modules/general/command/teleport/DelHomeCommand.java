package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

@ModuleCommand(
    module = "General",
    name = "delHome",
    defaultAliases = {})
public class DelHomeCommand {

  @Command(
      args = {},
      usage = {})
  public void delDefaultHome(ServerPlayer player) {
    delHome(
        player,
        PlayerUtils.getHome(
            player.local, ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).defaultHomeName));
  }

  @Command(
      args = {CommandArgument.HOME},
      usage = {"home"})
  public void delHome(ServerPlayer player, Home home) {
    List<Home> validHomes = new ArrayList<>();
    for (Home h : player.local.homes) {
      if (!h.name.equalsIgnoreCase(home.name)) {
        validHomes.add(h);
      }
    }
    player.local.homes = validHomes.toArray(new Home[0]);
    SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, player.local.uuid, player.local);
    ChatHelper.send(player.sender, player.lang.COMMAND_DELHOME.replaceAll("\\{@NAME@}", home.name));
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void delHomeDefaultOther(ServerPlayer player, EntityPlayer otherPlayer) {
    delHomeDefaultOther(
        player, otherPlayer, ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).defaultHomeName);
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.STRING},
      usage = {"player", "name"})
  public void delHomeDefaultOther(ServerPlayer player, EntityPlayer otherPlayer, String name) {
    delHomeOffline(player, otherPlayer.getDisplayNameString(), name);
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING},
      usage = {"player", "name"})
  public void delHomeOffline(ServerPlayer player, String otherPlayer, String name) {
    if (RankUtils.hasPermission(player.global, "command.delhome.other")) {
      String offlineUUID = PlayerUtils.getUUIDForInput(otherPlayer);
      if (offlineUUID != null) {
        LocalAccount local =
            SECore.dataLoader.get(
                DataLoader.DataType.LOCAL_ACCOUNT, offlineUUID, new LocalAccount());
        if (local != null) {
          List<Home> validHomes = new ArrayList<>();
          for (Home home : local.homes) {
            if (!home.name.equalsIgnoreCase(name)) {
              validHomes.add(home);
            }
          }
          local.homes = validHomes.toArray(new Home[0]);
          SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, offlineUUID, local);
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_DELHOME_OTHER
                  .replaceAll("\\{@NAME@}", name)
                  .replaceAll(
                      "\\{@PLAYER@}",
                      Objects.requireNonNull(PlayerUtils.getUsernameForInput(offlineUUID))));
        } else {
          ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
        }
      } else {
        ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
      }
    } else {
      ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
  }
}
