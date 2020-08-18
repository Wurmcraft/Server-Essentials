package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.Warp;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "warp", aliases = {"W"})
public class WarpCommand {

  @Command(inputArguments = {CommandArguments.WARP}, inputNames = {"Warp"})
  public void teleportToWarp(ICommandSender sender, Warp warp) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      if (RankUtils.hasPermission(sender, "general.warp." + warp.name)) {
        TeleportUtils.teleportTo((EntityPlayer) sender.getCommandSenderEntity(), warp);
        ChatHelper.sendMessage(sender,
            PlayerUtils.getLanguage(sender).GENERAL_WARP.replaceAll("%WARP%", warp.name));
      } else {
        TextComponentTranslation noPerms = new TextComponentTranslation(
            "commands.generic.permission", new Object[0]);
        noPerms.getStyle().setColor(TextFormatting.RED);
        ChatHelper.sendHoverMessage(sender, noPerms,
            TextFormatting.RED + "general.warp" + warp.name);
      }
    }
  }
}
