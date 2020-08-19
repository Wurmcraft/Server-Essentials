package com.wurmcraft.serveressentials.forge.modules.ban.command;


import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.rest.GlobalBan;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.UUID;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.UsernameCache;

@ModuleCommand(moduleName = "Ban", name = "globalpardon", aliases = {"GPardon"})
public class PardonGlobalBanCommand {

  @Command(inputArguments = {CommandArguments.STRING})
  public void banPlayer(ICommandSender sender, String player) {
    String uuid = "";
    try {
      uuid = UUID.fromString(player).toString();
    } catch (Exception e) {
      for (UUID id : UsernameCache.getMap().keySet()) {
        if (UsernameCache.getLastKnownUsername(id).equalsIgnoreCase(player)) {
          uuid = id.toString();
        }
      }
    }
    boolean found = false;
    if (!uuid.isEmpty()) {
      for (GlobalBan ban : RestRequestHandler.Ban.getGlobalBans()) {
        if (ban.uuid.equalsIgnoreCase(uuid)) {
          found = true;
          RestRequestHandler.Ban.deleteBan(ban);
          ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).BAN_UNBAN);
        }
      }
    }
    if (!found) {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_PLAYER_NONE
          .replaceAll("%PLAYER%", player));
    }
  }

}
