package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(module = "Chat", name = "Channel", defaultAliases = {"ch", "chan"})
public class ChannelCommand {

    @Command(args = {}, usage = {})
    public void defaultChannel(ServerPlayer player) {
        changeChannel(player, SECore.dataLoader.get(DataLoader.DataType.CHANNEL, ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel, new Channel()));
    }

    @Command(args = {CommandArgument.CHANNEL}, usage = "ChannelName")
    public void changeChannel(ServerPlayer player, Channel ch) {
        player.local.channel = ch.name;
        if (SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, player.player.getGameProfile().getId().toString(), player.local))
            ChatHelper.send(player.sender, player.lang.COMMAND_CHANNEL_CHANGED.replaceAll("\\{@CHANNEL@}", ch.name));
    }
}
