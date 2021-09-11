package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.ItemStackConverter;
import net.minecraft.item.ItemStack;

@ModuleCommand(module = "General", name = "Convert")
public class ConvertCommand {


    @Command(args = {}, usage = {}, isSubCommand = true, subCommandAliases = {"H"})
    public void hand(ServerPlayer player) {
        String hand = ItemStackConverter.toString(player.player.getHeldItemMainhand());
        ChatHelper.send(player.sender, player.lang.COMMAND_CONVERT_HAND.replaceAll("\\{@ITEM@}", hand));
    }
}
