package com.wurmcraft.serveressentials.forge.modules.ban.command;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.rest.GlobalBan;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(moduleName = "Ban", name = "globalban", aliases = {"GBan"})
public class GlobalBanCommand {

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING_ARR})
  public void banPlayer(ICommandSender sender, EntityPlayer player, String[] reason) {
    String banReason = String.join(" ", reason);
    EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
    GlobalBan ban = new GlobalBan(player.getDisplayNameString(),
        player.getGameProfile().getId().toString(),
        serverPlayer.connection.netManager.getRemoteAddress().toString(), banReason);
    RestRequestHandler.Ban.addGlobalBans(ban);
  }

  @Command(inputArguments = {CommandArguments.PLAYER, CommandArguments.STRING_ARR})
  public void banPlayer(ICommandSender sender, EntityPlayer player) {
    EntityPlayerMP serverPlayer = (EntityPlayerMP) player;
    GlobalBan ban = new GlobalBan(player.getDisplayNameString(),
        player.getGameProfile().getId().toString(),
        serverPlayer.connection.netManager.getRemoteAddress().toString());
    RestRequestHandler.Ban.addGlobalBans(ban);
  }
}
