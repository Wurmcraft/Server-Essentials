package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

@ModuleCommand(module = "Chat", name = "Reply", defaultAliases = "R")
public class ReplyCommand {

    @Command(args = {CommandArgument.STRING}, usage = "msg")
    public void reply(ServerPlayer player, String msg) {
        if (ChatHelper.lastMessageCache.containsKey(player.player.getGameProfile().getId())) {
            UUID lastMsg = ChatHelper.lastMessageCache.get(player.player.getGameProfile().getId());
            EntityPlayer otherPlayer = PlayerUtils.getFromUUID(lastMsg.toString());
            if (otherPlayer != null)
                ChatHelper.send(player.player, otherPlayer, msg);
            else
                ChatHelper.send(player.sender, player.lang.COMMAND_REPLY_INVALID);
        } else
            ChatHelper.send(player.sender, player.lang.COMMAND_REPLY_INVALID);
    }

    @Command(args = {CommandArgument.STRING_ARR}, usage = "msg")
    public void reply(ServerPlayer player, String[] msg) {
        reply(player, String.join(" ", msg));
    }
}
