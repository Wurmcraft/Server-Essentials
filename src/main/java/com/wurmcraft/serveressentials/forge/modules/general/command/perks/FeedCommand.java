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

@ModuleCommand(moduleName = "General", name = "Feed")
public class FeedCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void feedOther(ICommandSender sender, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(sender, "general.feed.other")) {
      otherPlayer.getFoodStats().setFoodSaturationLevel(20);
      otherPlayer.getFoodStats().setFoodLevel(20);
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_FEED_OTHER
          .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
      ChatHelper
          .sendMessage(otherPlayer, PlayerUtils.getLanguage(otherPlayer).GENERAL_FEED);
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper
          .sendHoverMessage(sender, noPerms, TextFormatting.RED + "general.feed.other");
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER})
  public void feed(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      player.getFoodStats().setFoodSaturationLevel(20);
      player.getFoodStats().setFoodLevel(20);
      ChatHelper
          .sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_FEED);
    }
  }
}
