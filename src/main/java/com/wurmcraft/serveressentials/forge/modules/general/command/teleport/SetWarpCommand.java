package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Warp;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "setwarp", aliases = {"swarp"})
public class SetWarpCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"warp"})
  public void newWarp(ICommandSender sender, String warp) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      SECore.dataHandler.registerData(DataKey.WARP,
          new Warp(warp, player.posX, player.posY, player.posZ, player.dimension));
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SETWARP);
    }
  }

  @Command(inputArguments = {CommandArguments.WARP}, inputNames = {"warp"})
  public void newWarp(ICommandSender sender, Warp warp) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      warp.x = player.posX;
      warp.y = player.posY;
      warp.z = player.posZ;
      warp.dim = player.dimension;
      SECore.dataHandler.registerData(DataKey.WARP, warp);
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SETWARP);
    }
  }

}
