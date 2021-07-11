package com.wurmcraft.serveressentials.common.modules.core.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import net.minecraft.util.text.TextComponentString;

@ModuleCommand(name = "se", module = "Core")
public class BaseCommand {

    @Command(args = {CommandArgument.STRING}, usage = {"version"})
    public void version (ServerPlayer player, String arg) {
        try {
            if (arg.equalsIgnoreCase("version")) {
                player.sender.sendMessage(new TextComponentString("Version: " + ServerEssentials.VERSION));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
