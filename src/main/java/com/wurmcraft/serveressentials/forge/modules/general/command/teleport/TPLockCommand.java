package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "tplock", aliases = {"tpaLock"})
public class TPLockCommand {

  @Command(inputArguments = {})
  public void toggleTPLock(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      StoredPlayer playerData = PlayerUtils.get(player);
      playerData.server.tpLock = !playerData.server.tpLock;
      if (playerData.server.tpLock) {
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPLOCK_ENABLE);
      } else {
        ChatHelper
            .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_TPLOCK_DISABLE);
      }
      SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    }
  }

}
