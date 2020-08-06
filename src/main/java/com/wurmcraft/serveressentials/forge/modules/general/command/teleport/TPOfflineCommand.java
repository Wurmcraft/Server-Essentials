package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.CommandArguments;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import java.util.NoSuchElementException;
import java.util.Objects;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

@ModuleCommand(moduleName = "General", name = "TPOffline", aliases = {"TPOff"})
public class TPOfflineCommand {

  @Command(inputArguments = {CommandArguments.STRING})
  public void teleportOffline(ICommandSender sender, String offlinePlayer) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      try {
        StoredPlayer playerData = PlayerUtils.get(
            Objects.requireNonNull(PlayerUtils.getPlayer(offlinePlayer)));
        if (playerData.server.lastLocation != null) {
          TeleportUtils.teleportTo(player, playerData.server.lastLocation);
          ChatHelper
              .sendHoverMessage(player,
                  PlayerUtils.getLanguage(player).GENERAL_TP_CORDS_OTHER,
                  TextFormatting.GOLD + "X: " + playerData.server.lastLocation.x + " Y: "
                      + playerData.server.lastLocation.y + " Z: "
                      + playerData.server.lastLocation.z + " Dim: "
                      + playerData.server.lastLocation.dim);
        }
      } catch (NoSuchElementException e) {
        ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).COMMAND_PLAYER_NONE
            .replaceAll("%PLAYER%", offlinePlayer));
      }
    }
  }
}
