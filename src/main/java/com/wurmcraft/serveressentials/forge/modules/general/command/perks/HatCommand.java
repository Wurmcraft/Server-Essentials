package com.wurmcraft.serveressentials.forge.modules.general.command.perks;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@ModuleCommand(moduleName = "General", name = "Hat")
public class HatCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void hat(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      if (!player.getHeldItemMainhand().isEmpty()) {
        ItemStack headStack = player.inventory.armorItemInSlot(0).copy();
        player.inventory.armorInventory.set(0, player.getHeldItemMainhand());
        player.inventory.addItemStackToInventory(headStack);
        ChatHelper
            .sendMessage(player, PlayerUtils.getLanguage(player).GENERAL_HAT
                .replaceAll("%ITEM%",
                    player.inventory.armorInventory.get(0).getDisplayName()));
      } else {
        ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).SIGN_NO_ITEM);
      }
    }
  }
}
