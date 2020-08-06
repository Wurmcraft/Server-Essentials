package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "Smite", aliases = {"Lightning"})
public class SmiteCommand {

  @Command(inputArguments = {})
  public void smiteSelf(ICommandSender sender) {
    sender.getEntityWorld().addWeatherEffect(
        new EntityLightningBolt(sender.getEntityWorld(), sender.getPosition().getX(),
            sender.getPosition().getY(), sender.getPosition().getZ(), false));
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SMITE);
  }

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void smitePlayer(ICommandSender sender, EntityPlayer player) {
    player.getEntityWorld().addWeatherEffect(
        new EntityLightningBolt(player.getEntityWorld(), player.getPosition().getX(),
            player.getPosition().getY(), player.getPosition().getZ(), false));
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SMITE_OTHER);
  }
}
