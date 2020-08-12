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

@ModuleCommand(moduleName = "General", name = "Heal")
public class HealCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void healOther(ICommandSender sender, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(sender, "general.heal.other")) {
      otherPlayer.setHealth(otherPlayer.getMaxHealth());
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_HEAL_OTHER
          .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
      ChatHelper
          .sendMessage(otherPlayer, PlayerUtils.getLanguage(otherPlayer).GENERAL_HEAL);
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper
          .sendHoverMessage(sender, noPerms, TextFormatting.RED + "general.heal.other");
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER})
  public void heal(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      player.setHealth(player.getMaxHealth());
      ChatHelper
          .sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_HEAL);
    }
  }
}
