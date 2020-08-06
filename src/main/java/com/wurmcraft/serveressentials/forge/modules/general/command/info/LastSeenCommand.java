package com.wurmcraft.serveressentials.forge.modules.general.command.info;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;

@ModuleCommand(moduleName = "General", name = "Seen", aliases = {"LastSeen"})
public class LastSeenCommand {

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void lastSeen(ICommandSender sender, EntityPlayer player) {
    StoredPlayer playerData = PlayerUtils.get(player);
    ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_LASTSEEN
        .replaceAll("%PLAYER%", player.getDisplayNameString())
        .replaceAll("%DATE%", "Now"));
  }

  @Command(inputArguments = {CommandArguments.PLAYER}, inputNames = {"Player"})
  public void lastSeen(ICommandSender sender, String player) {
    try {
      StoredPlayer playerData = PlayerUtils
          .get(UsernameCache.getLastKnownUsername(UUID.fromString(player)));
      ChatHelper.sendMessage(sender,
          PlayerUtils.getLanguage(sender).GENERAL_LASTSEEN.replaceAll("%PLAYER%",
              Objects.requireNonNull(
                  UsernameCache.getLastKnownUsername(UUID.fromString(player))))
              .replaceAll("%DATE%", new Date(playerData.global.lastSeen).toString()));
    } catch (NoSuchElementException e) {
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_PLAYER_NONE
          .replaceAll("%PLAYER%", player));
    }
  }
}
