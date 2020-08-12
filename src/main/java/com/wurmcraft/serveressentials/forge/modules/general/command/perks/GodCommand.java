package com.wurmcraft.serveressentials.forge.modules.general.command.perks;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "God")
public class GodCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void godOther(ICommandSender sender, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(sender, "general.god.other")) {
      otherPlayer.capabilities.disableDamage = !otherPlayer.capabilities.disableDamage;
      ChatHelper.sendMessage(sender, otherPlayer.capabilities.disableDamage ? PlayerUtils
          .getLanguage(sender).GENERAL_GODE_OTHER.replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()) : PlayerUtils
          .getLanguage(sender).GENERAL_GODD_OTHER
          .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
      ChatHelper.sendMessage(sender,
          otherPlayer.capabilities.allowFlying ? PlayerUtils.getLanguage(sender).GENERAL_GODE
              : PlayerUtils.getLanguage(sender).GENERAL_GODD);
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper
          .sendHoverMessage(sender, noPerms, TextFormatting.RED + "general.god.other");
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER})
  public void god(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      player.capabilities.disableDamage = !player.capabilities.disableDamage;
      ChatHelper.sendMessage(sender,
          player.capabilities.allowFlying ? PlayerUtils.getLanguage(sender).GENERAL_GODE
              : PlayerUtils.getLanguage(sender).GENERAL_GODD);
    }
  }
}
