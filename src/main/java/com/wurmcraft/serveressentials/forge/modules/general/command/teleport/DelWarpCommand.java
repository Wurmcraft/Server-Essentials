package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Warp;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;

@ModuleCommand(moduleName = "General", name = "delwarp", aliases = {"dWarp", "removeWarp",
    "remWarp", "rWarp"})
public class DelWarpCommand {

  @Command(inputArguments = {CommandArguments.WARP}, inputNames = {"warp"})
  public void deleteWarp(ICommandSender sender, Warp warp) {
    SECore.dataHandler.delData(DataKey.WARP, warp.getID(), true);
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_DELWARP);
  }
}
