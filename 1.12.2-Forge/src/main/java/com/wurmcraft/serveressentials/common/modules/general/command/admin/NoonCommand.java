package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Noon")
public class NoonCommand {

    public static final int time = 6000;

    @Command(args = {}, usage = {})
    public void setDay(ServerPlayer player) {
        FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().setWorldTime(time);
        ChatHelper.send(player.sender, player.lang.COMMAND_TIME.replaceAll("\\{@TIME@}", "' + time"));
    }
}

