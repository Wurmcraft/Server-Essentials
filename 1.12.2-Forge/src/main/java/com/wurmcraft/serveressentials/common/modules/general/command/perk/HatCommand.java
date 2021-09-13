package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.item.ItemStack;

@ModuleCommand(module = "General", name = "Hat")
public class HatCommand {

    @Command(args = {}, usage = {})
    public void placeHat(ServerPlayer player) {
        if(!player.player.inventory.armorInventory.get(3).isEmpty()) {
            player.player.inventory.addItemStackToInventory(player.player.inventory.armorInventory.get(3));
            player.player.inventory.armorInventory.set(3, ItemStack.EMPTY);
        }
        player.player.inventory.armorInventory.set(3, player.player.getHeldItemMainhand().splitStack(1));
        ChatHelper.send(player.sender, player.lang.COMMAND_HAT);
    }
}
