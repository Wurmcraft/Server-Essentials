package com.wurmcraft.serveressentials.common.modules.chat.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Chat", name = "Nick", defaultAliases = "Nickname")
public class NickCommand {

    @Command(args = {CommandArgument.STRING_ARR}, usage = "nick")
    public void nickSelf(ServerPlayer player, String[] arr) {
        String nickname = String.join(" ", arr);
        nickSelf(player, nickname);
    }

    @Command(args = {CommandArgument.STRING}, usage = "nick")
    public void nickSelf(ServerPlayer player, String nickname) {
        if (RankUtils.hasPermission(player.global, "command.nick.self")) {
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, player.local.uuid, new Account());
            account.display_name = nickname;
            SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, account.uuid, account);
            ChatHelper.send(player.sender, player.lang.COMMAND_NICK.replaceAll("\\{@NICK@}", account.display_name));
        } else {
            // TODO No Perms
        }
    }

    @Command(args = {}, usage = "")
    public void nickSelfReset(ServerPlayer player) {
        if (RankUtils.hasPermission(player.global, "command.nick.self")) {
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, player.local.uuid, new Account());
            account.display_name = "";
            SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, account.uuid, account);
            ChatHelper.send(player.sender, player.lang.COMMAND_NICK_RESET);
        } else {
            // TODO No Perms
        }
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING}, usage = {"player", "nick"}, canConsoleUse = true)
    public void nickOther(ServerPlayer player, EntityPlayer otherPlayer, String nick) {
        if (RankUtils.hasPermission(player.global, "command.nick.other")) {
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, otherPlayer.getGameProfile().getId().toString(), new Account());
            account.display_name = nick;
            SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, account.uuid, account);
            ChatHelper.send(player.sender, player.lang.COMMAND_NICK_OTHER.replaceAll("\\{@NICK@}", account.display_name).replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
        } else {
            // TODO No Perms
        }
    }

    @Command(args = {CommandArgument.PLAYER}, usage = "player", canConsoleUse = true)
    public void nickOtherReset(ServerPlayer player, EntityPlayer otherPlayer) {
        if (RankUtils.hasPermission(player.global, "command.nick.other")) {
            Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, otherPlayer.getGameProfile().getId().toString(), new Account());
            account.display_name = "";
            SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, account.uuid, account);
            ChatHelper.send(player.sender, player.lang.COMMAND_NICK_OTHER_RESET.replaceAll("\\{@PLAYER@}}", otherPlayer.getDisplayNameString()));
        } else {
            // TODO No Perms
        }
    }
}
