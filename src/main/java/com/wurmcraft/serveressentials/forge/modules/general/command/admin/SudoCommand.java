package com.wurmcraft.serveressentials.forge.modules.general.command.admin;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "Sudo", aliases = {"ForceRun"})
public class SudoCommand {

  @Command(inputArguments = {CommandArguments.PLAYER,
      CommandArguments.STRING_ARR}, inputNames = {"Player", "Msg"})
  public void forceUser(ICommandSender sender, EntityPlayer player, String[] command) {
    FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()
        .executeCommand(player,
            Strings.join(command, " "));
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SUDO
        .replaceAll("%PLAYER%", player.getDisplayNameString())
        .replaceAll("%ARGS%", Strings.join(command, "")));
  }

}
