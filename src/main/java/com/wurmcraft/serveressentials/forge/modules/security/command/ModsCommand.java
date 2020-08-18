package com.wurmcraft.serveressentials.forge.modules.security.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

@ModuleCommand(moduleName = "Security", name = "mods")
public class ModsCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"player"})
  public void playerMods(ICommandSender sender, EntityPlayer player) {
    EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
    ChatHelper.sendMessage(sender, TextFormatting.AQUA + String.join(", ",
        NetworkDispatcher.get(serverPlayer.connection.netManager).getModList().values()));
  }

}
