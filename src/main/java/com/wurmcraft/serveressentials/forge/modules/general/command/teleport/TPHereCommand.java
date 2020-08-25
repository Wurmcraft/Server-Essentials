package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "tphere")
public class TPHereCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void tpHere(ICommandSender sender, EntityPlayer toOther) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      TeleportUtils.teleportTo(toOther,
          new LocationWrapper(player.posX, player.posY, player.posZ, player.dimension));
      ChatHelper.sendMessage(toOther, PlayerUtils.getLanguage(toOther).GENERAL_TP_PLAYER
          .replaceAll("%PLAYER%", player.getDisplayNameString()));
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_TPHERE
          .replaceAll("%PLAYER%", toOther.getDisplayNameString()));
    }
  }
}
