package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;

@ModuleCommand(module = "General", name = "Repair", defaultAliases = {"Fix"})
public class RepairCommand {

    @Command(args = {}, usage = {})
    public void repair(ServerPlayer player) {
        if (!player.player.getHeldItemMainhand().isEmpty()) {
            if (player.player.getHeldItemMainhand().getMaxDamage() > player.player.getHeldItemMainhand().getItemDamage()) {
                player.player.getHeldItemMainhand().setItemDamage(0);
                ChatHelper.send(player.sender, player.lang.COMMAND_REPAIR);
            }
        } else
            ChatHelper.send(player.sender, player.lang.COMMAND_REPAIR_NONE);
    }
}
