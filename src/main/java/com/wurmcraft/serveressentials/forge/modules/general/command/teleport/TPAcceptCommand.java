package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "tpaccept", aliases = {"TPAccept"})
public class TPAcceptCommand {

  @Command(inputArguments = {})
  public static void accept(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (GeneralUtils.hasActiveTPARequest(player)) {
        Object[] tpaData = GeneralUtils.getActiveTPARequest(player);
        EntityPlayer otherPlayer = (EntityPlayer) tpaData[1];
        LocationWrapper playerLocation = new LocationWrapper(player.posX,
            player.posY, player.posZ, player.dimension);
        TeleportUtils.teleportTo(otherPlayer, playerLocation);
        ChatHelper.sendMessage(otherPlayer, PlayerUtils.getLanguage(otherPlayer).GENERAL_TPA
            .replaceAll("%NAME%", player.getDisplayNameString()));
      } else {
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPACCEPT_NONE);
      }
    }
  }
}
