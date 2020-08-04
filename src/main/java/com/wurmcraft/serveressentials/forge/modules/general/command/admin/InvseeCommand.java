package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.general.utils.PlayerInventory;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(moduleName = "General", name = "InvSee")
public class InvseeCommand {

  @Command(inputArguments = {CommandArguments.PLAYER})
  public void invseeOther(ICommandSender sender, EntityPlayer player) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer sendingPlayer = (EntityPlayer) sender.getCommandSenderEntity();
      if (player.openContainer != player.openContainer) {
        player.closeScreen();
      }
      sendingPlayer.displayGUIChest(new PlayerInventory((EntityPlayerMP) player, (EntityPlayerMP) sendingPlayer));
    }
  }
}
