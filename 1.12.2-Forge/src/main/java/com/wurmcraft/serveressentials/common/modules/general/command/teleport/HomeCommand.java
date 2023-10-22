package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(
    module = "General",
    name = "Home",
    defaultAliases = {"h"},
    defaultCooldown = "10s",
    defaultDelay = {"2s"})
public class HomeCommand {

  @Command(
      args = {},
      usage = {})
  public void defaultHome(ServerPlayer player) {
    Home home =
        PlayerUtils.getHome(
            player.local, ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).defaultHomeName);
    if (home != null) {
      home(player, home);
    } else {
      if (player.local.homes != null && player.local.homes.length > 0) {
        home(player, player.local.homes[0]);
      } else {
        ChatHelper.send(player.sender, player.lang.COMMAND_HOME_NONE);
      }
    }
  }

  @Command(
      args = {CommandArgument.HOME},
      usage = {"name"})
  public void home(ServerPlayer player, Home home) {
    if (home != null) {
      if (TeleportUtils.teleportTo((EntityPlayerMP) player.player, player.local, home, true)) {
        ChatHelper.send(
            player.sender, player.lang.COMMAND_HOME.replaceAll("\\{@NAME@}", home.name));
      } else {
        ChatHelper.send(
            player.sender,
            player.lang.TELEPORT_TIMER.replaceAll(
                "\\{@TIME@}",
                CommandUtils.displayTime(
                    (System.currentTimeMillis() - player.local.teleportTimer) / 1000)));
      }
    }
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = "list")
  public void home(ServerPlayer player, String arg) {
    if (arg.equalsIgnoreCase("list") || arg.equalsIgnoreCase("l")) {
      Home[] homes = player.local.homes;
      displayHomes(homes, player.sender, player.lang);
    } else if (arg.equalsIgnoreCase("set") || arg.equalsIgnoreCase("s")) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .commandManager
          .executeCommand(player.sender, "sethome " + arg);
    } else if (arg.equalsIgnoreCase("delete")
        || arg.equalsIgnoreCase("del")
        || arg.equalsIgnoreCase("d")
        || arg.equalsIgnoreCase("remove")
        || arg.equalsIgnoreCase("rem")
        || arg.equalsIgnoreCase("rm")
        || arg.equalsIgnoreCase("r")) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .commandManager
          .executeCommand(player.sender, "delHome " + arg);
    } else {
      for (Home home : player.local.homes) {
        if (arg.equalsIgnoreCase(home.name)) {
          home(player, home);
          return;
        }
      }
      ChatHelper.send(player.sender, player.lang.COMMAND_HOME_EXIST);
    }
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.STRING},
      usage = {"player", "name, list"})
  public void homeOther(ServerPlayer player, EntityPlayer otherPlayer, String arg) {
    homeOtherOffline(player, otherPlayer.getDisplayNameString(), arg);
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.STRING},
      usage = {"player", "name list"})
  public void homeOtherOffline(ServerPlayer player, String otherPlayer, String arg) {
    if (RankUtils.hasPermission(player.global, "command.home.other")) {
      String targetUUID = PlayerUtils.getUUIDForInput(otherPlayer);
      if (targetUUID != null) {
        LocalAccount local =
            SECore.dataLoader.get(
                DataLoader.DataType.LOCAL_ACCOUNT, targetUUID, new LocalAccount());
        if (local != null) {
          if (arg.equalsIgnoreCase("list") || arg.equalsIgnoreCase("l")) {
            displayHomes(local.homes, player.sender, player.lang);
          } else {
            for (Home home : local.homes) {
              if (arg.equalsIgnoreCase(home.name)) {
                if (TeleportUtils.teleportTo(
                    (EntityPlayerMP) player.player, player.local, home, true)) {
                  ChatHelper.send(
                      player.sender, player.lang.COMMAND_HOME.replaceAll("%NAME%", home.name));
                } else {
                  ChatHelper.send(
                      player.sender,
                      player.lang.TELEPORT_TIMER.replaceAll(
                          "%TIME%",
                          CommandUtils.displayTime(
                              (System.currentTimeMillis() - player.local.teleportTimer) / 1000)));
                }
                return;
              }
            }
            ChatHelper.send(player.sender, player.lang.COMMAND_HOME_EXIST);
          }
        } else {
          ChatHelper.send(
              player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", otherPlayer));
        }
      } else {
        ChatHelper.send(
            player.sender, player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", otherPlayer));
      }
    } else {
      ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
  }

  private static void displayHomes(Home[] homes, ICommandSender sender, Language lang) {
    ChatHelper.send(sender, lang.SPACER);
    for (Home home : homes) {
      ChatHelper.send(sender, homeInfo(home, lang));
    }
    ChatHelper.send(sender, lang.SPACER);
  }

  private static TextComponentString homeInfo(Home home, Language lang) {
    TextComponentString homeInfo =
        new TextComponentString(ChatHelper.replaceColor(lang.MESSAGE_COLOR + home.name));
    homeInfo.setStyle(
        homeInfo
            .getStyle()
            .setHoverEvent(
                new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new TextComponentString(
                        lang.HOME_OVER
                            .replaceAll("\\{@X@}", Integer.toString((int) Math.round(home.x)))
                            .replaceAll("\\{@Y@}", Integer.toString((int) Math.round(home.y)))
                            .replaceAll("\\{@Z@}", Integer.toString((int) Math.round(home.z)))
                            .replaceAll("\\{@DIM@}", Integer.toString(home.dim)))))
            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home.name)));
    return homeInfo;
  }
}
