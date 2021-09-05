package com.wurmcraft.serveressentials.common.modules.general.command.admin;

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

@ModuleCommand(module = "General", name = "Invsee", defaultAliases = {"isee"})
public class InvseeCommand {

    @Command(args = {}, usage = {})
    public void invsee(ServerPlayer player) {
        player.player.displayGUIChest(new PlayerInventory((EntityPlayerMP) player.player, (EntityPlayerMP) player.player, false));
    }

    @Command(args = {CommandArgument.PLAYER}, usage = {"player"})
    public void invsee(ServerPlayer player, EntityPlayer otherPlayer) {
        if (RankUtils.hasPermission(player.global, "command.invsee.other")) {
            player.player.displayGUIChest(new PlayerInventory((EntityPlayerMP) otherPlayer, (EntityPlayerMP) player.player, false));
        } else
            ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
}
