package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(module = "General", name = "Kill")
public class KillCommand {

    @Command(args = {CommandArgument.PLAYER}, usage = {"player"}, canConsoleUse = true)
    public void killPlayer(ServerPlayer player, EntityPlayerMP otherPlayer) {
        otherPlayer.setHealth(0);
        otherPlayer.setDead();
        ChatHelper.send(player.sender, player.lang.COMMAND_KILL.replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
    }
}
