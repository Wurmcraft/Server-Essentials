package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.Warp;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(module = "General", name = "SetWarp", defaultAliases = {"SWarp"})
public class SetWarpCommand {

    public static String[] NAME_BLACKLIST = {"list", "l", "delete", "del", "remove", "rem", "d", "r", "create", "c"};

    @Command(args = {CommandArgument.STRING}, usage = {"name"})
    public void setWarp(ServerPlayer player, String name) {
        // Check if name is invalid
        for (String n : NAME_BLACKLIST)
            if (n.equalsIgnoreCase(name)) {
                ChatHelper.send(player.sender, player.lang.COMMAND_SETWARP_INVALID.replaceAll("\\{@NAME@}", name));
                return;
            }
        // Create new warp
        Warp warp = new Warp(player.player.posX, player.player.posY, player.player.posZ, player.player.dimension, player.player.rotationPitch, player.player.rotationYaw, name);
        if (SECore.dataLoader.register(DataLoader.DataType.WARP, warp.name, warp))
            ChatHelper.send(player.sender, player.lang.COMMAND_SETWARP.replaceAll("\\{@NAME@}", warp.name));
        else if (SECore.dataLoader.update(DataLoader.DataType.WARP, warp.name, warp))
            ChatHelper.send(player.sender, player.lang.COMMAND_SETWARP.replaceAll("\\{@NAME@}", warp.name));
    }
}
