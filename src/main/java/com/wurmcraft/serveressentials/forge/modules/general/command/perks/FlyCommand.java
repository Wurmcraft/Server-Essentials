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

@ModuleCommand(moduleName = "General", name = "Fly")
public class FlyCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void flyOther(ICommandSender sender, EntityPlayer player) {
    if(RankUtils.hasPermission(sender, "general.fly.other")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        player.capabilities.allowFlying = !player.capabilities.allowFlying;
        ChatHelper.sendMessage(sender, player.capabilities.allowFlying ? PlayerUtils
            .getLanguage(sender).GENERAL_FLYE_OTHER
            .replaceAll("%PLAYER%", player.getDisplayNameString()) : PlayerUtils
            .getLanguage(sender).GENERAL_FLYD_OTHER
            .replaceAll("%PLAYER%", player.getDisplayNameString()));
        ChatHelper.sendMessage(player,
            player.capabilities.allowFlying ? PlayerUtils.getLanguage(player).GENERAL_FLYE
                : PlayerUtils.getLanguage(player).GENERAL_FLYD);
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.fly.other");
    }
  }

  @Command(inputArguments = {})
  public void fly(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      player.capabilities.allowFlying = !player.capabilities.allowFlying;
      ChatHelper.sendMessage(player,
          player.capabilities.allowFlying ? PlayerUtils.getLanguage(player).GENERAL_FLYE
              : PlayerUtils.getLanguage(player).GENERAL_FLYD);
    }
  }
}
