package com.wurmcraft.serveressentials.common.modules.general.command.perk;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;

@ModuleCommand(
    module = "General",
    name = "Fly",
    defaultAliases = {"ImABird", "Bird"})
public class FlyCommand {

  @Command(
      args = {},
      usage = {})
  public void fly(ServerPlayer player) {
    if (player.player.capabilities.allowFlying) {
      player.player.capabilities.allowFlying = false;
      player.player.capabilities.isFlying = false;
      player.player.sendPlayerAbilities();
      ChatHelper.send(player.sender, player.lang.COMMAND_FLY_OFF);
    } else {
      player.player.capabilities.allowFlying = true;
      player.player.capabilities.isFlying = true;
      player.player.sendPlayerAbilities();
      ChatHelper.send(player.sender, player.lang.COMMAND_FLY_ON);
    }
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void flyOther(ServerPlayer player, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(player.global, "command.fly.other")) {
      if (otherPlayer.capabilities.allowFlying) {
        otherPlayer.capabilities.allowFlying = false;
        otherPlayer.capabilities.isFlying = false;
        otherPlayer.sendPlayerAbilities();
        Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
        ChatHelper.send(otherPlayer, otherLang.COMMAND_FLY_OFF);
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_FLY_OFF_OTHER.replaceAll(
                "\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
      } else {
        otherPlayer.capabilities.allowFlying = true;
        otherPlayer.capabilities.isFlying = true;
        otherPlayer.sendPlayerAbilities();
        Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
        ChatHelper.send(otherPlayer, otherLang.COMMAND_FLY_ON);
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_FLY_ON_OTHER.replaceAll(
                "\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
      }
    } else {
      ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
  }
}
