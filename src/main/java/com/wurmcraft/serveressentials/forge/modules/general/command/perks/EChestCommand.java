package com.wurmcraft.serveressentials.forge.modules.general.command.perks;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.general.utils.PlayerInventory;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(moduleName = "General", name = "EChest", aliases = {"EnderChest"})
public class EChestCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void echestOther(ICommandSender sender, EntityPlayer player) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer sendingPlayer = (EntityPlayer) sender.getCommandSenderEntity();
      sendingPlayer.displayGUIChest(
          new PlayerInventory((EntityPlayerMP) player, (EntityPlayerMP) sendingPlayer,
              true));
    }
  }

  @Command(inputArguments = {})
  public void echest(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      player.displayGUIChest(
          new PlayerInventory((EntityPlayerMP) player, (EntityPlayerMP) player, true));
    }
  }
}
