package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(module = "Chat", name = "Broadcast", defaultAliases = "Bc")
public class BroadcastCommand {

    @Command(args = {CommandArgument.STRING_ARR}, usage = {"msg"}, canConsoleUse = true)
    public void broadcastMessage(ServerPlayer player, String[] arr) {
        String message = String.join(" ", arr);
        ChatHelper.sendToAll(message);
    }
}
