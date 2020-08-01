package com.wurmcraft.serveressentials.forge.modules.general.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "TPAaccept", aliases = {"TPAccept"})
public class TPAccept {

  @Command(inputArguments = {})
  public static void accept(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (GeneralModule.requestingTPA.containsKey(player.getGameProfile().getId())) {
        Object[] tpaData = GeneralModule.requestingTPA
            .get(player.getGameProfile().getId());
        EntityPlayer otherPlayer = (EntityPlayer) tpaData[0];
        LocationWrapper playerLocation = new LocationWrapper(otherPlayer.posX,
            otherPlayer.posY, otherPlayer.posZ, otherPlayer.dimension);
        TeleportUtils.teleportTo(player, playerLocation);
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPA.replaceAll("%NAME%", otherPlayer.getDisplayNameString()));
      } else {
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPACCEPT_NONE);
      }
    }
  }
}
