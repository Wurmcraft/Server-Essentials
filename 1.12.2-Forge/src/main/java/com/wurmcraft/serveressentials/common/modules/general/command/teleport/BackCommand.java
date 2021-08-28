package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(module = "General", name = "Back", defaultAliases = {"B"}, defaultCooldown = {"*:5s"})
public class BackCommand {

    @Command(args = {}, usage = {})
    public void back(ServerPlayer player) {
        if(player.local.lastLocation != null) {
            TeleportUtils.teleportTo((EntityPlayerMP) player.player, player.local, player.local.lastLocation);
            ChatHelper.send(player.sender, player.lang.COMMAND_BACK);
        }
    }
}
