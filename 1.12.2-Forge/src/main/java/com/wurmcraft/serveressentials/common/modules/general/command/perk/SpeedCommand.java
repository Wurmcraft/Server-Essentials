package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "General", name = "Speed", defaultAliases = {"Movement"})
public class SpeedCommand {

    @Command(args = {CommandArgument.DOUBLE}, usage = {"speed"})
    public void selfSpeed(ServerPlayer player, double speed) {
        selfSpeedType(player, "walk", speed);
        selfSpeedType(player, "fly", speed);
    }

    @Command(args = {CommandArgument.STRING, CommandArgument.DOUBLE}, usage = {"type", "speed"})
    public void selfSpeedType(ServerPlayer player, String type, double speed) {
        if (type.equalsIgnoreCase("walk") || type.equalsIgnoreCase("walking")) {
            player.player.capabilities.setPlayerWalkSpeed((float) (speed / 10));
            ChatHelper.send(player.sender, player.lang.COMMAND_SPEED_WALK.replaceAll("\\{@SPEED@}", speed + ""));
        } else if (type.equalsIgnoreCase("fly") || type.equalsIgnoreCase("flying")) {
            player.player.capabilities.setFlySpeed((float) (speed / 10));
            ChatHelper.send(player.sender, player.lang.COMMAND_SPEED_FLY.replaceAll("\\{@SPEED@}", speed + ""));
        }
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.DOUBLE}, usage = {"player", "speed"}, canConsoleUse = true)
    public void otherSpeed(ServerPlayer player, EntityPlayer otherPlayer, double speed) {
        otherSpeed(player, otherPlayer, "walk", speed);
        otherSpeed(player, otherPlayer, "fly", speed);
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING, CommandArgument.DOUBLE}, usage = {"player", "type", "speed"}, canConsoleUse = true)
    public void otherSpeed(ServerPlayer player, EntityPlayer otherPlayer, String type, double speed) {
        if (type.equalsIgnoreCase("walk") || type.equalsIgnoreCase("walking")) {
            otherPlayer.capabilities.setPlayerWalkSpeed((float) (speed / 10));
            Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
            ChatHelper.send(player.sender, player.lang.COMMAND_SPEED_WALK_OTHER.replaceAll("\\{@SPEED@}", speed + "").replaceAll("\\{@NAME@}", otherPlayer.getDisplayNameString()));
            ChatHelper.send(otherPlayer, otherLang.COMMAND_SPEED_WALK.replaceAll("\\{@SPEED@}", speed + ""));
        } else if (type.equalsIgnoreCase("fly") || type.equalsIgnoreCase("flying")) {
            otherPlayer.capabilities.setFlySpeed((float) (speed / 10));
            Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
            ChatHelper.send(player.sender, player.lang.COMMAND_SPEED_FLY_OTHER.replaceAll("\\{@SPEED@}", speed + "").replaceAll("\\{@NAME@}", otherPlayer.getDisplayNameString()));
            ChatHelper.send(otherPlayer, otherLang.COMMAND_SPEED_WALK.replaceAll("\\{@SPEED@}", speed + ""));
        }
    }
}
