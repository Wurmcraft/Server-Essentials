package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.general.event.GeneralEvents;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

@ModuleCommand(moduleName = "General", name = "Freeze", aliases = {"Bubble"})
public class FreezeCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void freezePlayer(ICommandSender sender, EntityPlayer player) {
    boolean isFrozen = GeneralEvents.isFrozen(player);
    if (isFrozen) {
      GeneralEvents.removeFrozen(player);
      ChatHelper
          .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_FREEZE_UNDO_OTHER);
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_FREEZE_UNDO);
    } else {
      GeneralEvents
          .addFrozen(player, new BlockPos(player.posX, player.posY, player.posZ));
      ChatHelper
          .sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_FREEZE_OTHER);
      ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_FREEZE);
    }
  }
}
