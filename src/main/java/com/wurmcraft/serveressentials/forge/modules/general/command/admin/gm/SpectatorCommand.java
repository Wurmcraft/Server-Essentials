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

@ModuleCommand(moduleName = "General", name = "Spectator", aliases = {"GMSP", "SP"})
public class SpectatorCommand {

  @Command(inputArguments = {})
  public void changeToAdventure(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      if (RankUtils.hasPermission(sender, "general.gamemode.spectator")) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        player.setGameType(GameType.SPECTATOR);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_SPECTATOR);
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission");
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper.sendHoverMessage(sender, noPerms,
            TextFormatting.RED + "general.gamemode.spectator");
      }
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void changeToAdventure(ICommandSender sender, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(sender, "general.gamemode.other")) {
      otherPlayer.setGameType(GameType.SPECTATOR);
      ChatHelper.sendMessage(otherPlayer,
          PlayerUtils.getLanguage(otherPlayer).GENERAL_GAMEMODE_SPECTATOR);
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_SPECTATOR_OTHER
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
