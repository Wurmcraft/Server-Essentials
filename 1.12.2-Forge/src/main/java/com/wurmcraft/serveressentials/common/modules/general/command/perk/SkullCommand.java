package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

@ModuleCommand(module = "General", name = "Skull", defaultAliases = {"Head"})
public class SkullCommand {

    @Command(args = {}, usage = {})
    public void skullSelf(ServerPlayer player) {
        skull(player, player.player.getGameProfile().getName());
    }

    @Command(args = {CommandArgument.STRING}, usage = {"name"})
    public void skull(ServerPlayer player, String name) {
        ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setTag("SkullOwner", new NBTTagString(name));
        if (player.player.inventory.addItemStackToInventory(stack))
            ChatHelper.send(player.sender, player.lang.COMMAND_SKULL.replaceAll("\\{@PLAYER@}", name));
    }
}
