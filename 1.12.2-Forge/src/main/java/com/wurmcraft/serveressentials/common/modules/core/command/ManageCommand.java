package com.wurmcraft.serveressentials.common.modules.core.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(module = "Core", name = "Manage", defaultSecure = true)
public class ManageCommand {

    @Command(args = {CommandArgument.DATA_TYPE, CommandArgument.STRING}, usage = {"type", "key"}, isSubCommand = true, subCommandAliases = {"r"})
    public void reload(ServerPlayer player, DataLoader.DataType type, String key) {
        boolean status = SECore.dataLoader.get(type, key) != null;
        if (status) {
            SECore.dataLoader.delete(type, key, true);
            Object data = SECore.dataLoader.get(type, key);
            if (data != null) {
                ChatHelper.send(player.sender, player.lang.COMMAND_MANAGE_RELOAD.replaceAll("\\{@DATA_TYPE@}", type.name()).replaceAll("\\{@KEY@}", key));
                return;
            }
            ChatHelper.send(player.sender, player.lang.COMMAND_MANAGE_RELOAD_FAIL.replaceAll("\\{@DATA_TYPE@}", type.name()).replaceAll("\\{@KEY@}", key));
        } else
            ChatHelper.send(player.sender, player.lang.COMMAND_MANAGE_STATUS_NEG);
    }

    @Command(args = {CommandArgument.DATA_TYPE, CommandArgument.STRING}, usage = {"type", "key"}, isSubCommand = true, subCommandAliases = {"s"})
    public void status(ServerPlayer player, DataLoader.DataType type, String key) {
        boolean status = SECore.dataLoader.get(type, key) != null;
        ChatHelper.send(player.sender, status ? player.lang.COMMAND_MANAGE_STATUS : player.lang.COMMAND_MANAGE_STATUS_NEG);
    }
}
