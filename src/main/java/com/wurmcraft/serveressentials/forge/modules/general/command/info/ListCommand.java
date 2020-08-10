package com.wurmcraft.serveressentials.forge.modules.general.command.info;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.rest.ServerStatus;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(moduleName = "General", name = "List", aliases = {"OnlinePlayers",
    "Players"})
public class ListCommand {

  @Command(inputArguments = {})
  public void listPlayers(ICommandSender sender) {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
      for (ServerStatus status : RestRequestHandler.Track.getStatus()) {
        ChatHelper.sendMessage(sender, TextFormatting.LIGHT_PURPLE + status.serverID);
        for (String player : status.players) {
          ChatHelper.sendMessage(sender,
              " - " + TextFormatting.GOLD + player.substring(0, player.indexOf("(") - 1));
        }
      }
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
    } else {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
      for (String player :
          FMLCommonHandler.instance().getMinecraftServerInstance()
              .getOnlinePlayerNames()) {
        ChatHelper.sendMessage(sender,
            " - " + TextFormatting.GOLD + player.substring(0, player.indexOf("(") - 1));
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_SPACER);
      }
    }
  }

}
