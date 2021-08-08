package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Chat", name = "DM", defaultAliases = {"Msg", "M", "Pm"})
public class DMCommand {

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING_ARR}, usage = {"player", "msg"})
    public void msg(ServerPlayer player, EntityPlayer otherPlayer, String[] msg) {
        msg(player, otherPlayer, String.join(" ", msg));
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING}, usage = {"player", "msg"})
    public void msg(ServerPlayer player, EntityPlayer otherPlayer, String msg) {
        ChatHelper.send(player.player, otherPlayer, msg);
    }

    // TODO Send message to remote users
}
