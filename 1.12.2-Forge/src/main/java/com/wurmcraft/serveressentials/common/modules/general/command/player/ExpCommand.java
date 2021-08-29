package com.wurmcraft.serveressentials.common.modules.general.command.player;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "General", name = "Experience", defaultAliases = {"Exp"})
public class ExpCommand {

    @Command(args = {CommandArgument.INTEGER}, usage = {"amount"})
    public void selfExp(ServerPlayer player, int lvl) {
        player.player.addExperienceLevel(lvl);
        ChatHelper.send(player.sender, player.lang.COMMAND_EXP.replaceAll("\\{@LVL@}", "" + lvl));
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.INTEGER}, usage = {"player", "amount"}, canConsoleUse = true)
    public void expOther(ServerPlayer player, EntityPlayer otherPlayer, int lvl) {
        otherPlayer.addExperienceLevel(lvl);
        Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
        ChatHelper.send(otherPlayer, otherLang.COMMAND_EXP.replaceAll("\\{@LVL@}", "" + lvl));
        ChatHelper.send(player.sender, player.lang.COMMAND_EXP_OTHER.replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()).replaceAll("\\{@LVL@}", "" + lvl));
    }
}
