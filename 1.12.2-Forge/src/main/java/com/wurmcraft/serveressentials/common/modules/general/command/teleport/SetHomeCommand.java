package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

@ModuleCommand(
    module = "General",
    name = "SetHome",
    defaultAliases = {})
public class SetHomeCommand {

  @Command(
      args = {},
      usage = {})
  public void setDefaultHome(ServerPlayer player) {
    setHome(player, ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).defaultHomeName);
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"name"})
  public void setHome(ServerPlayer player, String name) {
    int maxHomes = PlayerUtils.maxHomes(player.global);
    boolean isOverride = PlayerUtils.getHome(player.local, name) != null;
    if (player.local.homes != null && player.local.homes.length < maxHomes
        || isOverride
        || player.local.homes == null) {
      Home newHome =
          new Home(
              player.player.posX,
              player.player.posY,
              player.player.posZ,
              player.player.dimension,
              player.player.rotationPitch,
              player.player.rotationYaw,
              name);
      // Add home to player
      if (isOverride) {
        for (int index = 0; index < player.local.homes.length; index++) {
          if (player.local.homes[index].name.equalsIgnoreCase(name)) {
            player.local.homes[index] = newHome;
            break;
          }
        }
      } else {
        if (player.local.homes == null) {
          player.local.homes = new Home[0];
        }
        player.local.homes =
            Arrays.copyOfRange(player.local.homes, 0, player.local.homes.length + 1);
        player.local.homes[player.local.homes.length - 1] = newHome;
      }
      SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, player.local.uuid, player.local);
      // Send formatted message
      TextComponentString homeMSG =
          new TextComponentString(
              ChatHelper.replaceColor(player.lang.COMMAND_SETHOME.replaceAll("\\{@NAME@}", name)));
      homeMSG.setStyle(
          homeMSG
              .getStyle()
              .setHoverEvent(
                  new HoverEvent(
                      HoverEvent.Action.SHOW_TEXT,
                      new TextComponentString(
                          player
                              .lang
                              .HOME_OVER
                              .replaceAll("\\{@X@}", Integer.toString((int) Math.round(newHome.x)))
                              .replaceAll("\\{@Y@}", Integer.toString((int) Math.round(newHome.y)))
                              .replaceAll("\\{@Z@}", Integer.toString((int) Math.round(newHome.z)))
                              .replaceAll("\\{@DIM@}", Integer.toString(newHome.dim)))))
              .setClickEvent(
                  new ClickEvent(ClickEvent.Action.RUN_COMMAND, "home " + newHome.name)));
      ChatHelper.send(player.sender, homeMSG);
    } else {
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_SETHOME_MAX.replaceAll("\\{@MAX@}", Integer.toString(maxHomes)));
    }
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.STRING},
      usage = {"player", "name"})
  public void setHomeOther(ServerPlayer player, EntityPlayerMP otherPlayer, String name) {
    setHomeOffline(player, otherPlayer.getDisplayNameString(), name);
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING},
      usage = {"player", "name"})
  public void setHomeOffline(ServerPlayer player, String offlineUser, String name) {
    if (RankUtils.hasPermission(player.global, "command.home.other")) {
      String offlineUUID = PlayerUtils.getUUIDForInput(offlineUser);
      if (offlineUUID != null) {
        int maxHomes =
            PlayerUtils.maxHomes(
                SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, offlineUUID, new Account()));
        LocalAccount local =
            SECore.dataLoader.get(
                DataLoader.DataType.LOCAL_ACCOUNT, offlineUUID, new LocalAccount());
        if (local == null) {
          ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
          return;
        }
        boolean isOverride = PlayerUtils.getHome(local, name) != null;
        if (local.homes.length < maxHomes || isOverride) {
          Home newHome =
              new Home(
                  player.player.posX,
                  player.player.posY,
                  player.player.posZ,
                  player.player.dimension,
                  player.player.rotationPitch,
                  player.player.rotationYaw,
                  name);
          // Add home to player
          if (isOverride) {
            for (int index = 0; index < local.homes.length; index++) {
              if (local.homes[index].name.equalsIgnoreCase(name)) {
                local.homes[index] = newHome;
                return;
              }
            }
          } else {
            local.homes = Arrays.copyOfRange(local.homes, 0, local.homes.length + 1);
            local.homes[local.homes.length - 1] = newHome;
          }
          SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, local.uuid, local);
          // Send formatted message
          TextComponentString homeMSG =
              new TextComponentString(
                  player
                      .lang
                      .COMMAND_SETHOME_OTHER
                      .replaceAll("\\{@NAME@}", name)
                      .replaceAll(
                          "\\{@PLAYER@}",
                          Objects.requireNonNull(PlayerUtils.getUsernameForInput(offlineUUID))));
          homeMSG.setStyle(
              homeMSG
                  .getStyle()
                  .setHoverEvent(
                      new HoverEvent(
                          HoverEvent.Action.SHOW_TEXT,
                          new TextComponentString(
                              player
                                  .lang
                                  .HOME_OVER
                                  .replaceAll(
                                      "\\{@X@}", Integer.toString((int) Math.round(newHome.x)))
                                  .replaceAll(
                                      "\\{@Y@}", Integer.toString((int) Math.round(newHome.y)))
                                  .replaceAll(
                                      "\\{@Z@}", Integer.toString((int) Math.round(newHome.z)))
                                  .replaceAll("\\{@DIM@}", Integer.toString(newHome.dim))))));
          ChatHelper.send(player.sender, homeMSG);
        } else {
          ChatHelper.send(
              player.sender,
              player.lang.COMMAND_SETHOME_MAX.replaceAll("\\{@MAX@}", Integer.toString(maxHomes)));
        }
      } else {
        ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND);
      }
    } else {
      ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
  }
}
