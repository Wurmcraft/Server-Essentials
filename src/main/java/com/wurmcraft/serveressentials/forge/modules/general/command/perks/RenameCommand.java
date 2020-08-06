package com.wurmcraft.serveressentials.forge.modules.general.command.perks;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(moduleName = "General", name = "Rename", aliases = {"Name"})
public class RenameCommand {

  @Command(inputArguments = {CommandArguments.STRING_ARR}, inputNames = {"Rename_Args"})
  public void renameItem(ICommandSender sender, String[] args) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ((EntityPlayer) sender.getCommandSenderEntity())
          .getHeldItemMainhand()
          .setStackDisplayName(Strings.join(args, " ").replaceAll("&", "\u00a7"));
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(player).GENERAL_RENAME);
    }
  }
}
