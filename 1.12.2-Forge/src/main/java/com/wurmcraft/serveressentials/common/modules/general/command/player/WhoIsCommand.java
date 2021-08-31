package com.wurmcraft.serveressentials.common.modules.general.command.player;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "General", name = "Whois", defaultAliases = {"Who", ""})
public class WhoIsCommand {

    @Command(args = {CommandArgument.PLAYER}, usage = {"player"})
    public void whois(ServerPlayer player, EntityPlayer otherPlayer) {
        Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, otherPlayer.getGameProfile().getId().toString(), new Account());
        ChatHelper.send(player.sender, player.lang.COMMAND_WHOIS_UUID.replaceAll("\\{@UUID}", otherPlayer.getGameProfile().getId().toString()));
        ChatHelper.send(player.sender, player.lang.COMMAND_WHOIS_NAME.replaceAll("\\{@NAME@}", otherPlayer.getDisplayNameString()));
        ChatHelper.send(player.sender, player.lang.COMMAND_WHOIS_DISPLAYNAME.replaceAll("\\{@NICK@}", account.displayName));
    }
}
