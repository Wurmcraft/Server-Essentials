package com.wurmcraft.serveressentials.common.modules.general.command.utils;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(module = "General", name = "Ping", defaultAliases = {"Alive"})
public class PingCommand {

    @Command(args = {}, usage = {})
    public void ping(ServerPlayer player) {
        ChatHelper.send(player.sender, player.lang.COMMAND_PING);
    }
}
