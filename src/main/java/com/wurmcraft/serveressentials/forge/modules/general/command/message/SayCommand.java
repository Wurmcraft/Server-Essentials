package com.wurmcraft.serveressentials.forge.modules.general.command.message;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "Broadcast", aliases = {"BC"})
public class SayCommand {

  @Command(inputArguments = {CommandArguments.STRING_ARR}, inputNames = {"MSG"})
  public void sayCommand(ICommandSender sender, String[] msg) {
    for (EntityPlayerMP p : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      ChatHelper.sendMessage(p, "[Server] \\u00BB" + Strings.join(msg, " "));
    }
  }
}
