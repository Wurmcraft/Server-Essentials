package com.wurmcraft.serveressentials.forge.modules.security.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "Security", name = "lockdown")
public class LockDownCommand {

  public static boolean lockdown = false;

  @Command(inputArguments = {})
  public void lockdown(ICommandSender sender) {
    lockdown = !lockdown;
    if (lockdown) {
      for(EntityPlayer player: FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).SECURITY_LOCKDOWN_ENABLED);
      }
    } else {
      for(EntityPlayer player: FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).SECURITY_LOCKDOWN_DISABLED);
      }
    }
  }
}
