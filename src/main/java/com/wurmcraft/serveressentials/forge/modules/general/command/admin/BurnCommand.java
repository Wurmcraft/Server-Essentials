package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "Burn", aliases = {"Ignite"})
public class BurnCommand {


  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void burnPlayer(ICommandSender sender, EntityPlayer player) {
    burnPlayer(sender, player, 20);
  }

  @Command(inputArguments = {CommandArguments.PLAYER,
      CommandArguments.INTEGER}, inputNames = {"Player", "Time"})
  public void burnPlayer(ICommandSender sender, EntityPlayer player, int time) {
    player.setFire(time);
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_BURN_OTHER);
  }
}
