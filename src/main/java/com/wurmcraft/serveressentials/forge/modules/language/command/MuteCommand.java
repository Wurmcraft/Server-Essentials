package com.wurmcraft.serveressentials.forge.modules.language.command;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.command.SECommand;
import com.wurmcraft.serveressentials.forge.server.command.WrapperCommand;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "mute")
public class MuteCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"player"})
  public void mute(ICommandSender sender, EntityPlayer player) {
    if (RankUtils.hasPermission(sender, "language.mute.perm")) {
      StoredPlayer data = PlayerUtils.get(player);
      boolean currentStatus = data.global.muted;
      if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
        GlobalPlayer global = RestRequestHandler.User
            .getPlayer(player.getGameProfile().getId().toString());
        data.global = global;
        currentStatus = data.global.muted;
      }
      if (currentStatus) {
        data.global.muted = false;
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).LANGUAGE_MUTE_UNDO);
        RestRequestHandler.User
            .overridePlayer(player.getGameProfile().getId().toString(), data.global);
      } else {
        data.global.muted = true;
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).LANGUAGE_MUTE);
        ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).LANGUAGE_MUTED);
        RestRequestHandler.User
            .overridePlayer(player.getGameProfile().getId().toString(), data.global);
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "language.mute.perm");
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.INTEGER,
      CommandArguments.STRING}, inputNames = {"player", "time", "Sec, Min, Hour, Day"})
  public void mute(ICommandSender sender, EntityPlayer player, int time, String type) {
    StoredPlayer data = PlayerUtils.get(player);
    boolean currentStatus = data.global.muted;
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      GlobalPlayer global = RestRequestHandler.User
          .getPlayer(player.getGameProfile().getId().toString());
      currentStatus = global.muted;
    }
    if (currentStatus) {
      data.global.muted = false;
      ChatHelper
          .sendMessage(sender, PlayerUtils.getLanguage(sender).LANGUAGE_MUTE_UNDO);
      RestRequestHandler.User
          .overridePlayer(player.getGameProfile().getId().toString(), data.global);
    } else {
      data.global.muted = true;
      long multiplier = 1;
      if (type.equalsIgnoreCase("Sec") || type.equalsIgnoreCase("Second") || type
          .equalsIgnoreCase("Seconds")) {
        multiplier = 1000;
      } else if (type.equalsIgnoreCase("Min") | type.equalsIgnoreCase("Minute") || type
          .equalsIgnoreCase("Minutes")) {
        multiplier = 60000;
      } else if (type.equalsIgnoreCase("Hour") || type.equalsIgnoreCase("Hours")) {
        multiplier = 3600000;
      } else if (type.equalsIgnoreCase("Day") || type.equalsIgnoreCase("Days")) {
        multiplier = 86400000;
      }
      long expire = time * multiplier;
      data.global.muteExpire = System.currentTimeMillis() + expire;
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).LANGUAGE_MUTE);
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).LANGUAGE_MUTED);
      RestRequestHandler.User
          .overridePlayer(player.getGameProfile().getId().toString(), data.global);
    }
  }
}
