package com.wurmcraft.serveressentials.common.modules.security.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

@ModuleCommand(module = "Security", name = "mods")
public class ModsCommand {

  @Command(args = {CommandArgument.PLAYER}, usage = {"player"})
  public void playerMods(ICommandSender sender, EntityPlayer player) {
    EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
    ChatHelper.send(sender, TextFormatting.AQUA + String.join(", ",
        NetworkDispatcher.get(serverPlayer.connection.netManager).getModList().values()));
  }
}
