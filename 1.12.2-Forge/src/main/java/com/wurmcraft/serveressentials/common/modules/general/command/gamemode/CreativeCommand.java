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

@ModuleCommand(module = "General", name = "Creative", defaultAliases = {"Gmc"})
public class CreativeCommand {

    @Command(args = {}, usage = {})
    public void creative(ServerPlayer player) {
        player.player.setGameType(GameType.CREATIVE);
        ChatHelper.send(player.sender, player.lang.COMMAND_CREATIVE);
    }

    @Command(args = {CommandArgument.PLAYER}, usage = {"player"})
    public void creativeOther(ServerPlayer player, EntityPlayer otherPlayer) {
        otherPlayer.setGameType(GameType.CREATIVE);
        Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
        ChatHelper.send(otherPlayer, otherLang.COMMAND_CREATIVE);
        ChatHelper.send(player.sender, player.lang.COMMAND_CREATIVE_OTHER.replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
    }
}
