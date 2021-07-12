package com.wurmcraft.serveressentials.common.modules.core.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(name = "se", module = "Core")
public class BaseCommand {

    @Command(args = {CommandArgument.STRING}, usage = {"version, modules"})
    public void arg(ServerPlayer player, String arg) {
        if (arg.equalsIgnoreCase("version"))
            ChatHelper.send(player.sender, player.lang.COMMAND_BASE_VERSION.replaceAll("\\{@VERSION@}", ServerEssentials.VERSION));
    }
}
