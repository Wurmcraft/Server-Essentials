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

@ModuleCommand(module = "General", name = "Heal")
public class HealCommand {

  @Command(
      args = {},
      usage = {})
  public void healSelf(ServerPlayer player) {
    player.player.setHealth(Integer.MAX_VALUE);
    ChatHelper.send(player.sender, player.lang.COMMAND_HEAL);
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void healOther(ServerPlayer player, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(player.global, "command.heal.other")) {
      otherPlayer.setHealth(Integer.MAX_VALUE);
      Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
      ChatHelper.send(otherPlayer, otherLang.COMMAND_HEAL);
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_HEAL_OTHER.replaceAll(
              "\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
    } else
      ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
  }
}
