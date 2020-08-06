package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.general.event.GeneralEvents;
import com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "Vanish", aliases = {"V"})
public class VanishCommand {

  @Command(inputArguments = {})
  public void vanish(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      boolean inVanish = GeneralEvents.vanishedPlayers.contains(player);
      if (inVanish) {
        GeneralEvents.vanishedPlayers.remove(player);
        GeneralUtils.updateVanish(player, true);
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_VANISH_UNDO);
      } else {
        GeneralEvents.vanishedPlayers.add(player);
        GeneralUtils.updateVanish(player, false);
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_VANISH);
      }
    }
  }

}
