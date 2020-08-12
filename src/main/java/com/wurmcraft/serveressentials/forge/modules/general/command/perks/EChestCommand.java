package com.wurmcraft.serveressentials.forge.modules.general.command.perks;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.modules.general.utils.PlayerInventory;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.command.SECommand;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "EChest", aliases = {"EnderChest"})
public class EChestCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void echestOther(ICommandSender sender, EntityPlayer player) {
    if(RankUtils.hasPermission(sender, "general.echest.other")) {
      if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
        EntityPlayer sendingPlayer = (EntityPlayer) sender.getCommandSenderEntity();
        sendingPlayer.displayGUIChest(
            new PlayerInventory((EntityPlayerMP) player, (EntityPlayerMP) sendingPlayer,
                true));
      }
    } else {
      TextComponentTranslation noPerms = new TextComponentTranslation(
          "commands.generic.permission", new Object[0]);
      noPerms.getStyle().setColor(TextFormatting.RED);
      ChatHelper.sendHoverMessage(sender, noPerms,
          TextFormatting.RED + "general.echest.other");
    }
  }

  @Command(inputArguments = {})
  public void echest(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      player.displayGUIChest(
          new PlayerInventory((EntityPlayerMP) player, (EntityPlayerMP) player, true));
    }
  }
}
