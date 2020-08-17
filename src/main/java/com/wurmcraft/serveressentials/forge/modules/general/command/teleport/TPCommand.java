package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.command.SECommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "tp", aliases = {"Teleport"})
public class TPCommand {

  @Command(inputArguments = {CommandArguments.INTEGER, CommandArguments.INTEGER,
      CommandArguments.INTEGER, CommandArguments.INTEGER})
  public void teleportToLocation(ICommandSender sender, int x, int y, int z, int dim) {
    if (RankUtils.hasPermission(sender, "general.tp.cords")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        TeleportUtils.teleportTo(player, new LocationWrapper(x, y, z, dim));
        ChatHelper
            .sendHoverMessage(player, PlayerUtils.getLanguage(player).GENERAL_TP_CORDS,
                TextFormatting.GOLD + "X: " + x + " Y: " + y + " Z: " + z + " Dim: "
                    + dim);
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.tp.cords");
    }
  }

  @Command(inputArguments = {CommandArguments.INTEGER, CommandArguments.INTEGER,
      CommandArguments.INTEGER})
  public void teleportToLocation(ICommandSender sender, int x, int y, int z) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      teleportToLocation(sender, x, y, z, player.dimension);
    }
  }

  @Command(inputArguments = {CommandArguments.INTEGER, CommandArguments.INTEGER})
  public void teleportToLocation(ICommandSender sender, int x, int z) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      teleportToLocation(sender, x,
          player.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY(), z,
          player.dimension);
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.INTEGER,
      CommandArguments.INTEGER,
      CommandArguments.INTEGER, CommandArguments.INTEGER})
  public void teleportToLocation(ICommandSender sender, EntityPlayer otherPlayer, int x,
      int y, int z, int dim) {
    if (RankUtils.hasPermission(sender, "general.tp.cords_other")) {
      TeleportUtils.teleportTo(otherPlayer, new LocationWrapper(x, y, z, dim));
      ChatHelper
          .sendHoverMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TP_CORDS,
              TextFormatting.GOLD + "X: " + x + " Y: " + y + " Z: " + z + " Dim: "
                  + dim);
      ChatHelper
          .sendHoverMessage(otherPlayer,
              PlayerUtils.getLanguage(otherPlayer).GENERAL_TP_CORDS_OTHER,
              TextFormatting.GOLD + "X: " + x + " Y: " + y + " Z: " + z + " Dim: "
                  + dim);
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.tp.cords_other");
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.INTEGER,
      CommandArguments.INTEGER,
      CommandArguments.INTEGER})
  public void teleportToLocation(ICommandSender sender, EntityPlayer otherPlayer, int x,
      int y, int z) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      teleportToLocation(sender, otherPlayer, x, y, z, player.dimension);
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.INTEGER,
      CommandArguments.INTEGER})
  public void teleportToLocation(ICommandSender sender, EntityPlayer otherPlayer, int x,
      int z) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      teleportToLocation(sender, otherPlayer, x,
          player.world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY(), z,
          player.dimension);
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.PLAYER})
  public void teleportPlayerToPlayer(ICommandSender sender, EntityPlayer player,
      EntityPlayer to) {
    if (RankUtils.hasPermission(sender, "general.tp.player")) {
      TeleportUtils
          .teleportTo(player,
              new LocationWrapper(to.posX, to.posY, to.posZ, to.dimension));
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).GENERAL_TP_PLAYER_PLAYER
              .replaceAll("%PLAYER%", player.getDisplayNameString())
              .replaceAll("%PLAYER2%", to.getDisplayNameString()));
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_TP_PLAYER
          .replaceAll("%PLAYER%", to.getDisplayNameString()));
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.tp.player");
    }
  }

  @Command(inputArguments = {CommandArguments.PLAYER})
  public void teleportPlayerToPlayer(ICommandSender sender, EntityPlayer to) {
    if (RankUtils.hasPermission(sender, "general.tp.player")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
        TeleportUtils.teleportTo(player,
            new LocationWrapper(to.posX, to.posY, to.posZ, to.dimension));
        ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_TP_PLAYER
            .replaceAll("%PLAYER%", to.getDisplayNameString()));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.tp.player");
    }
  }
}
