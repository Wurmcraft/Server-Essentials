package com.wurmcraft.serveressentials.forge.modules.general.command.admin.gm;

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
import net.minecraft.world.GameType;

@ModuleCommand(moduleName = "General", name = "Creative", aliases = {"GMC", "C"})
public class CreativeCommand {

  @Command(inputArguments = {})
  public void changeToCreative(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      if (RankUtils.hasPermission(sender, "general.gamemode.creative")) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        player.setGameType(GameType.CREATIVE);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_CREATIVE);
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission");
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper.sendHoverMessage(sender, noPerms,
            TextFormatting.RED + "general.gamemode.creative");
      }
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void changeToCreative(ICommandSender sender, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(sender, "general.gamemode.other")) {
      otherPlayer.setGameType(GameType.CREATIVE);
      ChatHelper.sendMessage(otherPlayer,
          PlayerUtils.getLanguage(otherPlayer).GENERAL_GAMEMODE_CREATIVE);
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_CREATIVE_OTHER
              .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.gamemode.other");
    }
  }
}
