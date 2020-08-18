package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "top")
public class TopCommand {

  public static final int MAX_Y = 256;

  @Command(inputArguments = {})
  public void top(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      for(int y = 0; y < MAX_Y; y++)  {
        if(player.world.isAirBlock(player.getPosition().add(0,y,0)) && player.world.isAirBlock(player.getPosition().add(0,y + 1,0))) {
          TeleportUtils.teleportTo(player, new LocationWrapper(player.posX, player.posY + y, player.posZ,player.dimension));
          ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TOP);
        }
      }
    }
  }
}
