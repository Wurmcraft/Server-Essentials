package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(
    module = "General",
    name = "Tp",
    defaultAliases = {"Teleport"})
public class TPCommand {

  @Command(
      args = {CommandArgument.PLAYER},
      usage = "player")
  public void teleportTo(ServerPlayer player, EntityPlayer otherPlayer) {
    TeleportUtils.teleportTo(
        (EntityPlayerMP) player.player,
        player.local,
        new Location(
            otherPlayer.posX,
            otherPlayer.posY,
            otherPlayer.posZ,
            otherPlayer.dimension,
            player.player.rotationPitch,
            player.player.rotationYaw));
    ChatHelper.send(
        player.player,
        player.lang.COMMAND_TP.replaceAll("\\{@PLAYER@}",
            otherPlayer.getDisplayNameString()));
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.PLAYER},
      usage = {"teleportPlayer", "target"},
      canConsoleUse = true)
  public void teleportPlayer2Player(
      ServerPlayer player, EntityPlayer teleportee, EntityPlayer target) {
    if (teleportee == null || target == null) {
      ChatHelper.send(player.sender,
          player.lang.PLAYER_NOT_FOUND.replaceAll("\\{@PLAYER@}", "none"));
      return;
    }
    TeleportUtils.teleportTo(
        (EntityPlayerMP) teleportee,
        SECore.dataLoader.get(
            DataLoader.DataType.LOCAL_ACCOUNT,
            teleportee.getGameProfile().getId().toString(),
            new LocalAccount()),
        new Location(
            target.posX,
            target.posY,
            target.posZ,
            target.dimension,
            teleportee.rotationPitch,
            teleportee.rotationYaw));
    ChatHelper.send(
        player.player,
        player
            .lang
            .COMMAND_TP_OTHER
            .replaceAll("\\{@PLAYER@}", teleportee.getDisplayNameString())
            .replaceAll("\\{@PLAYER2@}", target.getDisplayNameString()));
    Language otherLang = CommandUtils.getPlayerLang(teleportee);
    ChatHelper.send(teleportee, otherLang.COMMAND_TPHERE_OTHER);
  }
}
