package com.wurmcraft.serveressentials.common.modules.general.command.misc;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Say")
public class SayCommand {

  @Command(args = {CommandArgument.STRING_ARR}, usage = {"MSG"})
  public void sayCommand(ServerPlayer sender, String[] msg) {
    for (EntityPlayerMP p : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList().getPlayers()) {
      ChatHelper.send(p, "&6[Server] &b" + Strings.join(msg, " "));
    }
  }

  @Command(args = {CommandArgument.STRING}, usage = {"MSG"})
  public void sayCommand(ServerPlayer sender, String msg) {
   sayCommand(sender, new String[] {msg});
  }
}
