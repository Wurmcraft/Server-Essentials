package com.wurmcraft.serveressentials.forge.modules.general.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(moduleName = "General", name = "Ping", aliases = {""})
public class PingCommand {

  @Command(inputArguments = {})
  public void ping(ICommandSender sender) {
    int ping = 0;
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();
      ping = player.ping;
    }
    ChatHelper.sendMessage(sender,
        PlayerUtils.getLanguage(sender).GENERAL_PING.replaceAll("%PING%", "" + ping));
  }

}
