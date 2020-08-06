package com.wurmcraft.serveressentials.forge.modules.general.command.perks;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

@ModuleCommand(moduleName = "General", name = "Skull")
public class SkullCommand {

  @Command(inputArguments = {CommandArguments.STRING}, inputNames = {"Player"})
  public void skull(ICommandSender sender, String playerName) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
      stack.setTagCompound(new NBTTagCompound());
      stack.getTagCompound().setTag("SkullOwner", new NBTTagString(playerName));
      ((EntityPlayer) sender.getCommandSenderEntity()).inventory
          .addItemStackToInventory(stack);
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_SKULL
          .replaceAll("%SKULL%", playerName));
    }
  }

}
