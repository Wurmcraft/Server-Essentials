package com.wurmcraft.serveressentials.common.modules.general.command.misc;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Say")
public class SayCommand {

  @Command(args = {CommandArgument.STRING_ARR}, usage = {"MSG"})
  public void sayCommand(ICommandSender sender, String[] msg) {
    for (EntityPlayerMP p : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      ChatHelper.send(p, "[Server] \\u00BB" + Strings.join(msg, " "));
    }
  }
}
