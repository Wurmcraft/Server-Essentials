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
    name = "Feed",
    defaultAliases = {"Food", "ImTooLazyToEatSoYouDoIt"})
public class FeedCommand {

  @Command(
      args = {},
      usage = {})
  public void feedSelf(ServerPlayer player) {
    player.player.getFoodStats().setFoodLevel(20);
    player.player.getFoodStats().setFoodSaturationLevel(50);
    ChatHelper.send(player.sender, player.lang.COMMAND_FEED);
  }

  @Command(
      args = {CommandArgument.PLAYER},
      usage = {"player"})
  public void feedOther(ServerPlayer player, EntityPlayer otherPlayer) {
    if (RankUtils.hasPermission(player.global, "command.feed.other")) {
      otherPlayer.getFoodStats().setFoodLevel(20);
      otherPlayer.getFoodStats().setFoodSaturationLevel(50);
      Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_FEED_OTHER.replaceAll(
              "\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
      ChatHelper.send(otherPlayer, otherLang.COMMAND_FEED);
    } else
      ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
  }
}
