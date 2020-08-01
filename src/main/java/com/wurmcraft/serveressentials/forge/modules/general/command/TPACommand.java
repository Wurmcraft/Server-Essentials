package com.wurmcraft.serveressentials.forge.modules.general.command;

import static com.wurmcraft.serveressentials.forge.modules.general.GeneralModule.requestingTPA;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "Tpa")
public class TPACommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"player"})
  public void tpaPlayer(ICommandSender sender, EntityPlayer otherPlayer) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      requestingTPA
          .put(otherPlayer.getGameProfile().getId(), new Object[] {player, System.currentTimeMillis()});
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPA_SENT
          .replaceAll("%PLAYER%", otherPlayer.getDisplayNameString()));
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_TPA_REQUEST
          .replaceAll("%PLAYER%", player.getDisplayNameString()));
    }
  }
}
