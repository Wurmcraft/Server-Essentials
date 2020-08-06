package com.wurmcraft.serveressentials.forge.modules.general.command.perks;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;

@ModuleCommand(moduleName = "General", name = "Speed")
public class SpeedCommand {

  @Command(inputArguments = {CommandArguments.DOUBLE}, inputNames = {"Speed"})
  public void setSpeedBoth(ICommandSender sender, double amount) {
    setSpeedBoth(sender, "Both", amount);
  }

  @Command(inputArguments = {CommandArguments.INTEGER}, inputNames = {"Speed"})
  public void setSpeedBoth(ICommandSender sender, int amount) {
    setSpeedBoth(sender, "Both", amount);
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.DOUBLE}, inputNames = {"Fly, Walk, Both", "Speed"})
  public void setSpeedBoth(ICommandSender sender, String type, double amount) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (type.equalsIgnoreCase("Both")) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        player.capabilities.writeCapabilitiesToNBT(tagCompound);
        tagCompound
            .getCompoundTag("abilities")
            .setTag("flySpeed", new NBTTagFloat((float) amount / 10));
        tagCompound
            .getCompoundTag("abilities")
            .setTag("walkSpeed", new NBTTagFloat((float) amount / 10));
        player.capabilities.readCapabilitiesFromNBT(tagCompound);
        player.sendPlayerAbilities();
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SPEED
            .replaceAll("%AMOUNT%", amount + ""));
      } else if (type.equalsIgnoreCase("Fly")) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        player.capabilities.writeCapabilitiesToNBT(tagCompound);
        tagCompound
            .getCompoundTag("abilities")
            .setTag("flySpeed", new NBTTagFloat((float) amount / 10));
        player.capabilities.readCapabilitiesFromNBT(tagCompound);
        player.sendPlayerAbilities();
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SPEED_WALK
            .replaceAll("%AMOUNT%", amount + ""));
      } else if (type.equalsIgnoreCase("Walk")) {
        NBTTagCompound tagCompound = new NBTTagCompound();
        player.capabilities.writeCapabilitiesToNBT(tagCompound);
        tagCompound
            .getCompoundTag("abilities")
            .setTag("walkSpeed", new NBTTagFloat((float) amount / 10));
        player.capabilities.readCapabilitiesFromNBT(tagCompound);
        player.sendPlayerAbilities();
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SPEED_FLY
            .replaceAll("%AMOUNT%", amount + ""));
      } else {
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Speed")
                .replaceAll("%ARGS%", "<Both, Fly, Walk> <amount>"));
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).COMMAND_USAGE.replaceAll("%COMMAND%", "Speed")
                .replaceAll("%ARGS%", "<Player> <Both, Fly, Walk> <amount>"));
      }
    }
  }

  @Command(inputArguments = {CommandArguments.STRING,
      CommandArguments.INTEGER}, inputNames = {"Fly, Walk, Both", "Speed"})
  public void setSpeedBoth(ICommandSender sender, String type, int amount) {
    setSpeedBoth(sender, type, (double) amount);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING,
      CommandArguments.INTEGER}, inputNames = {"Player", "Fly, Walk, Both", "Speed"})
  public void setOtherSeed(ICommandSender sender, EntityPlayer player, String type,
      int amount) {
    setOtherSeed(sender, player, type, (double) amount);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING,
      CommandArguments.DOUBLE}, inputNames = {"Player", "Fly, Walk, Both", "Speed"})
  public void setOtherSeed(ICommandSender sender, EntityPlayer player, String type,
      double amount) {
    setSpeedBoth(player, type, amount);
      if(type.equalsIgnoreCase("Both")) {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SPEED_OTHER.replaceAll("%AMOUNT%", ""+amount));
      } else  if(type.equalsIgnoreCase("Fly")) {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SPEED_FLY_OTHER.replaceAll("%AMOUNT%", ""+amount));
      }else  if(type.equalsIgnoreCase("Walk")) {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SPEED_WALK_OTHER.replaceAll("%AMOUNT%", ""+amount));
      }
  }

  @Command(inputArguments = {CommandArguments.PLAYER,
      CommandArguments.INTEGER}, inputNames = {"Player", "Speed"})
  public void setOtherSpeed(ICommandSender sender, EntityPlayer player, int amount) {
    setOtherSeed(sender, player, "Both", (double) amount);
  }

  @Command(inputArguments = {CommandArguments.PLAYER,
      CommandArguments.DOUBLE}, inputNames = {"Player", "Speed"})
  public void setOtherSpeed(ICommandSender sender, EntityPlayer player, double amount) {
    setOtherSeed(sender, player, "Both", amount);
  }
}
