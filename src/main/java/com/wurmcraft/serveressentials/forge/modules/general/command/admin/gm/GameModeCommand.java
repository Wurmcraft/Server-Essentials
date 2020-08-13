package com.wurmcraft.serveressentials.forge.modules.general.command.admin.gm;


import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

@ModuleCommand(moduleName = "General", name = "gamemode", aliases = {"Gm", "Mode"})
public class GameModeCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"Mode"})
  public void changeMode(ICommandSender sender, String mode) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (mode.equalsIgnoreCase("Creative") || mode.startsWith("C")) {
        changeMode(player, 1);
      } else if (mode.equalsIgnoreCase("Survival") || mode.equalsIgnoreCase("S")) {
        changeMode(player, 0);
      } else if (mode.equalsIgnoreCase("Adventure") || mode.equalsIgnoreCase("A")) {
        changeMode(player, 2);
      } else if (mode.equalsIgnoreCase("Spectator") || mode.equalsIgnoreCase("SP")) {
        changeMode(player, 3);
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_INVAID
                .replaceAll("%MODE%", mode));
      }
    }
  }

  @Command(inputArguments = {CommandArguments.INTEGER}, inputNames = {"Mode"})
  public void changeMode(ICommandSender sender, int no) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (no == 0 && RankUtils.hasPermission(sender, "general.gamemode.survival")) {
        player.setGameType(GameType.SURVIVAL);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_SURVIVAL);
      } else if (no == 1 && RankUtils
          .hasPermission(sender, "general.gamemode.creative")) {
        player.setGameType(GameType.CREATIVE);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_CREATIVE);
      } else if (no == 2 && RankUtils
          .hasPermission(sender, "general.gamemode.adventure")) {
        player.setGameType(GameType.ADVENTURE);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_ADVENTURE);
      } else if (no == 3 && RankUtils
          .hasPermission(sender, "general.gamemode.spectator")) {
        player.setGameType(GameType.SPECTATOR);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_SPECTATOR);
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_INVAID
                .replaceAll("%MODE%", "" + no));
      }
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER,
      CommandArguments.STRING}, inputNames = {"Player", "Mode"})
  public void changeOtherRank(ICommandSender sender, EntityPlayerMP player, String mode) {
    if (RankUtils.hasPermission(sender, "general.gamemode.other")) {
      if (mode.equalsIgnoreCase("Creative") || mode.startsWith("C")) {
        changeOtherRank(sender, player, 1);
      } else if (mode.equalsIgnoreCase("Survival") || mode.equalsIgnoreCase("S")) {
        changeOtherRank(sender, player, 0);
      } else if (mode.equalsIgnoreCase("Adventure") || mode.startsWith("A")) {
        changeOtherRank(sender, player, 2);
      } else if (mode.equalsIgnoreCase("Spectator") || mode.startsWith("SP")) {
        changeOtherRank(sender, player, 3);
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_INVAID
                .replaceAll("%MODE%", mode));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.gamemode.X");
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.INTEGER},
      inputNames = {"Player", "Mode"})
  public void changeOtherRank(ICommandSender sender, EntityPlayerMP player, int mode) {
    if (RankUtils.hasPermission(sender, "general.gamemode.other")) {
      if (mode == 1) {
        player.setGameType(GameType.CREATIVE);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_CREATIVE);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_CREATIVE_OTHER
                .replaceAll("%PLAYER%", player.getDisplayNameString()));
      } else if (mode == 0) {
        player.setGameType(GameType.SURVIVAL);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_SURVIVAL);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_SURVIVAL_OTHER
                .replaceAll("%PLAYER%", player.getDisplayNameString()));
      } else if (mode == 2) {
        player.setGameType(GameType.ADVENTURE);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_ADVENTURE);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_ADVENTURE_OTHER
                .replaceAll("%PLAYER%", player.getDisplayNameString()));
      } else if (mode == 3) {
        player.setGameType(GameType.SPECTATOR);
        ChatHelper.sendMessage(player,
            PlayerUtils.getLanguage(player).GENERAL_GAMEMODE_SPECTATOR);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_SPECTATOR_OTHER
                .replaceAll("%PLAYER%", player.getDisplayNameString()));
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_GAMEMODE_INVAID
                .replaceAll("%MODE%", "" + mode));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.gamemode.X");
    }
  }

}
