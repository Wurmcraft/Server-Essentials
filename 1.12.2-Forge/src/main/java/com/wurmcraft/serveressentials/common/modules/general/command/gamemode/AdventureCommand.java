package com.wurmcraft.serveressentials.common.modules.general.command.gamemode;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

@ModuleCommand(module = "General", name = "Adventure", defaultAliases = {"Gma"})
public class AdventureCommand {

    @Command(args = {}, usage = {})
    public void adventure(ServerPlayer player) {
        player.player.setGameType(GameType.ADVENTURE);
        ChatHelper.send(player.sender, player.lang.COMMAND_ADVENTURE);
    }

    @Command(args = {CommandArgument.PLAYER}, usage = {"player"})
    public void adventureOther(ServerPlayer player, EntityPlayer otherPlayer) {
        otherPlayer.setGameType(GameType.ADVENTURE);
        Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
        ChatHelper.send(otherPlayer, otherLang.COMMAND_ADVENTURE);
        ChatHelper.send(player.sender, player.lang.COMMAND_ADVENTURE_OTHER.replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
    }
}
