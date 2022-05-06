package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Chat", name = "PauseChat", defaultAliases = "Pc")
public class PauseChatCommand {

    @Command(args = {}, usage = {}, canConsoleUse = true)
    public void pauseAllChat(ServerPlayer player) {
        Channel[] channels = SECore.dataLoader.getFromKey(DataLoader.DataType.CHANNEL, new Channel()).values().toArray(new Channel[0]);
        for (Channel ch : channels)
            pauseChannel(player, ch);
    }

    @Command(args = {CommandArgument.CHANNEL}, usage = {"channel"}, canConsoleUse = true)
    public void pauseChannel(ServerPlayer player, Channel ch) {
        if (ch.enabled) {
            ch.enabled = false;
            SECore.dataLoader.update(DataLoader.DataType.CHANNEL, ch.name, ch);
            ChatHelper.send(player.sender, player.lang.COMMAND_PAUSECHAT_DISABLED.replaceAll("\\{@CHANNEL@}", ch.name));
        } else {
            ch.enabled = true;
            SECore.dataLoader.update(DataLoader.DataType.CHANNEL, ch.name, ch);
            ChatHelper.send(player.sender, player.lang.COMMAND_PAUSECHAT_ENABLED.replaceAll("\\{@CHANNEL@}", ch.name));
        }
        // Update all current users about channel status
        for (EntityPlayer serverPlayer : ChatHelper.getInChannel(ch).keySet()) {
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, serverPlayer.getGameProfile().getId().toString(), new Account());
            Language lang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, account.lang, new Language());
            if (ch.enabled)
                ChatHelper.send(serverPlayer, lang.COMMAND_PAUSECHAT_ANNOUCMENT_ENABLED.replaceAll("\\{@CHANNEL@}", ch.name));
            else
                ChatHelper.send(serverPlayer, lang.COMMAND_PAUSECHAT_ANNOUCMENT_DISABLED.replaceAll("\\{@CHANNEL@}", ch.name));
        }
    }
}
