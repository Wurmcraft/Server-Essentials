package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;

@ModuleCommand(
    module = "General",
    name = "Speed",
    defaultAliases = {"Movement"})
public class SpeedCommand {

  @Command(
      args = {CommandArgument.DOUBLE},
      usage = {"speed"})
  public void selfSpeed(ServerPlayer player, double speed) {
    selfSpeedType(player, "walk", speed);
    selfSpeedType(player, "fly", speed);
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.DOUBLE},
      usage = {"type", "speed"})
  public void selfSpeedType(ServerPlayer player, String type, double speed) {
    if (type.equalsIgnoreCase("walk") || type.equalsIgnoreCase("walking")) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      player.player.capabilities.writeCapabilitiesToNBT(tagCompound);
      if (type.equalsIgnoreCase("walking")) {
        type = "walk";
      }
      tagCompound.getCompoundTag("abilities")
          .setTag(type.toLowerCase() + "Speed", new NBTTagFloat((float) speed / 10));
      player.player.capabilities.readCapabilitiesFromNBT(tagCompound);
      player.player.sendPlayerAbilities();
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_SPEED_WALK.replaceAll("\\{@SPEED@}", speed + ""));
    } else if (type.equalsIgnoreCase("fly") || type.equalsIgnoreCase("flying")) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      player.player.capabilities.writeCapabilitiesToNBT(tagCompound);
      if (type.equalsIgnoreCase("flying")) {
        type = "fly";
      }
      tagCompound.getCompoundTag("abilities")
          .setTag(type.toLowerCase() + "Speed", new NBTTagFloat((float) speed / 10));
      player.player.capabilities.readCapabilitiesFromNBT(tagCompound);
      player.player.sendPlayerAbilities();
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_SPEED_FLY.replaceAll("\\{@SPEED@}", speed + ""));
    }
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.DOUBLE},
      usage = {"player", "speed"},
      canConsoleUse = true)
  public void otherSpeed(ServerPlayer player, EntityPlayer otherPlayer, double speed) {
    otherSpeed(player, otherPlayer, "walk", speed);
    otherSpeed(player, otherPlayer, "fly", speed);
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.STRING, CommandArgument.DOUBLE},
      usage = {"player", "type", "speed"},
      canConsoleUse = true)
  public void otherSpeed(ServerPlayer player, EntityPlayer otherPlayer, String type,
      double speed) {
    if (type.equalsIgnoreCase("walk") || type.equalsIgnoreCase("walking")) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      otherPlayer.capabilities.writeCapabilitiesToNBT(tagCompound);
      if (type.equalsIgnoreCase("walking")) {
        type = "walk";
      }
      tagCompound.getCompoundTag("abilities")
          .setTag(type.toLowerCase() + "Speed", new NBTTagFloat((float) speed / 10));
      otherPlayer.capabilities.readCapabilitiesFromNBT(tagCompound);
      otherPlayer.sendPlayerAbilities();
      Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
      ChatHelper.send(
          player.sender,
          player
              .lang
              .COMMAND_SPEED_WALK_OTHER
              .replaceAll("\\{@SPEED@}", speed + "")
              .replaceAll("\\{@NAME@}", otherPlayer.getDisplayNameString()));
      ChatHelper.send(
          otherPlayer,
          otherLang.COMMAND_SPEED_WALK.replaceAll("\\{@SPEED@}", speed + ""));
    } else if (type.equalsIgnoreCase("fly") || type.equalsIgnoreCase("flying")) {
      NBTTagCompound tagCompound = new NBTTagCompound();
      otherPlayer.capabilities.writeCapabilitiesToNBT(tagCompound);
      if (type.equalsIgnoreCase("flying")) {
        type = "fly";
      }
      tagCompound.getCompoundTag("abilities")
          .setTag(type.toLowerCase() + "Speed", new NBTTagFloat((float) speed / 10));
      otherPlayer.capabilities.readCapabilitiesFromNBT(tagCompound);
      otherPlayer.sendPlayerAbilities();
      Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
      ChatHelper.send(
          player.sender,
          player
              .lang
              .COMMAND_SPEED_FLY_OTHER
              .replaceAll("\\{@SPEED@}", speed + "")
              .replaceAll("\\{@NAME@}", otherPlayer.getDisplayNameString()));
      ChatHelper.send(
          otherPlayer,
          otherLang.COMMAND_SPEED_WALK.replaceAll("\\{@SPEED@}", speed + ""));
    }
  }
}
