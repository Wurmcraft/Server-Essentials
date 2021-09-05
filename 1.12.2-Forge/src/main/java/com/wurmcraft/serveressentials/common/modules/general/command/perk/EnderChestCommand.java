package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.modules.general.utils.inventory.PlayerInventory;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

@ModuleCommand(module = "General", name = "EnderChest", defaultAliases = {"EChest"})
public class EnderChestCommand {

    @Command(args = {}, usage = {})
    public void echest(ServerPlayer player) {
        player.player.displayGUIChest(new PlayerInventory((EntityPlayerMP) player.player, (EntityPlayerMP) player.player, true));
    }

    @Command(args = {CommandArgument.PLAYER}, usage = {"player"})
    public void echest(ServerPlayer player, EntityPlayer otherPlayer) {
        if (RankUtils.hasPermission(player.global, "command.echest.other")) {
            player.player.displayGUIChest(new PlayerInventory((EntityPlayerMP) otherPlayer, (EntityPlayerMP) player.player, true));
        } else
            ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
}
