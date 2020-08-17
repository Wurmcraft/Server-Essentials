package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import static com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils.requestingTPA;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "tpa")
public class TPACommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"player"})
  public void tpaPlayer(ICommandSender sender, EntityPlayer otherPlayer) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      requestingTPA.add(new Object[]{(System.currentTimeMillis() + (GeneralModule.config.tpaRequestTimeout * 1000)), otherPlayer, player});
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPA_SENT
          .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
      ChatHelper.sendMessage(otherPlayer,
          PlayerUtils.getLanguage(otherPlayer).GENERAL_TPA_REQUEST
              .replaceAll("%PLAYER%", player.getDisplayNameString()));
    }
  }
}
