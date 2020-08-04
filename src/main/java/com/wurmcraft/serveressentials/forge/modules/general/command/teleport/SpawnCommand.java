package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "Spawn")
public class SpawnCommand {

  @Command(inputArguments = {})
  public void spawn(ICommandSender sender) {
    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if(SECore.config.spawn != null) {
        TeleportUtils.teleportTo(player, SECore.config.spawn);
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SPAWN);
      } else {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_SPAWN_NONE);
      }
    }
  }
}
