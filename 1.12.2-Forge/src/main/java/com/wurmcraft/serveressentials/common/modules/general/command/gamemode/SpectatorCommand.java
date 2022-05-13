package com.wurmcraft.serveressentials.common.modules.general.command.gamemode;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

@ModuleCommand(
    module = "General",
    name = "Spectator",
    defaultAliases = {"GmSp"})
public class SpectatorCommand {

  @Command(
      args = {},
      usage = {})
  public void spectator(ServerPlayer player) {
    player.player.setGameType(GameType.SPECTATOR);
    ChatHelper.send(player.sender, player.lang.COMMAND_SPECTATOR);
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void spectatorOther(ServerPlayer player, EntityPlayer otherPlayer) {
    otherPlayer.setGameType(GameType.SPECTATOR);
    Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
    ChatHelper.send(otherPlayer, otherLang.COMMAND_SPECTATOR);
    ChatHelper.send(
        player.sender,
        player.lang.COMMAND_SPECTATOR_OTHER.replaceAll(
            "\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
  }
}
