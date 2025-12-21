package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Reward;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.command.perk.VaultCommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.RewardGenerator;
import java.util.List;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@ModuleCommand(
    module = "Economy",
    name = "Reward",
    defaultAliases = {},
    defaultCooldown = "*:5")
public class RewardCommand {

  @Command(
      args = {},
      usage = {})
  public void _claim(ServerPlayer player) {
    claim(player, 0);
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"points"},
      isSubCommand = true)
  public void claim(ServerPlayer player, int tier) {
    if (player.global.reward_points > 0) {
      ItemStack reward = RewardGenerator.generateRandomReward(tier);
      if (player.player.inventory.addItemStackToInventory(reward)) {
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_REWARD_GIVEN.replaceAll("\\{@ITEM@}", reward.getDisplayName()));
      } else {
        VaultCommand.addToMailbox(player.player, reward);
        ChatHelper.send(
            player.sender,
            player.lang.COMMAND_REWARD_GIVEN_VAULT.replaceAll(
                "\\{@ITEM@}", reward.getDisplayName()));
      }
      SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, player.global.uuid, player.global);
    } else {
      ChatHelper.send(player.sender, player.lang.COMMAND_REWARD_NO_POINTS);
    }
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.INTEGER, CommandArgument.INTEGER},
      usage = {"add,remove", "chance", "tier"},
      isSubCommand = true)
  public void admin(ServerPlayer player, String arg, int chance, int tier) {
    if (arg.equalsIgnoreCase("add")
        || arg.equalsIgnoreCase("a")
        || arg.equalsIgnoreCase("create") | arg.equalsIgnoreCase("cre")
        || arg.equalsIgnoreCase("c")) {
      String hand = ServerEssentials.stackConverter.toString(player.player.getHeldItemMainhand());
      if (player.player.getHeldItemMainhand().isEmpty()) {
        ChatHelper.send(player.sender, player.lang.COMMAND_BALANCE_EMPTY);
        return;
      }
      Reward newReward = new Reward(hand, chance, tier);
      if (RewardGenerator.isValidReward(newReward)) {
        RewardGenerator.addNewReward(new Reward(hand, chance, tier));
        ChatHelper.send(
            player.sender,
            player
                .lang
                .COMMAND_REWARD_CREATED
                .replaceAll("\\{@ITEM@}", player.player.getHeldItemMainhand().getDisplayName())
                .replaceAll("\\{@CHANCE@}", String.valueOf(chance))
                .replaceAll("\\{@TIER@}", String.valueOf(tier)));
      } else {
        ChatHelper.send(player.sender, player.lang.COMMAND_REWARD_INVALID);
      }
    }
  }

  @Command(
      args = {CommandArgument.STRING},
      usage = {"remove", "list"},
      isSubCommand = true,
      canConsoleUse = true)
  public void admin(ServerPlayer player, String arg) {
    if (arg.equalsIgnoreCase("remove")
        || arg.equalsIgnoreCase("rem")
        || arg.equalsIgnoreCase("r")
        || arg.equalsIgnoreCase("delete")
        || arg.equalsIgnoreCase("del")
        || arg.equalsIgnoreCase("d")) {
      if (player.player.getHeldItemMainhand().isEmpty()) {
        ChatHelper.send(player.sender, player.lang.COMMAND_BALANCE_EMPTY);
        return;
      }
      if (RewardGenerator.removeReward(player.player.getHeldItemMainhand())) {
        ChatHelper.send(player.sender, player.lang.COMMAND_REWARD_REMOVED);
      } else {
        ChatHelper.send(player.sender, player.lang.COMMAND_REWARD_REMOVED_NONE);
      }
    }
  }

  @Command(
      args = {CommandArgument.PLAYER, CommandArgument.INTEGER},
      usage = {
        "player", "amount",
      },
      isSubCommand = true,
      canConsoleUse = true)
  public void admin(ServerPlayer player, EntityPlayer otherPlayer, int amount) {
    Account account = PlayerUtils.getLatestAccount(otherPlayer.getGameProfile().getId().toString());
    if (account != null) {
      account.reward_points = account.reward_points + amount;
      ChatHelper.send(
          player.sender,
          player.lang.COMMAND_REWARD_POINTS_SET.replaceAll(
              "\\{@AMOUNT@}", String.valueOf(account.reward_points)));
    }
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"tier"},
      isSubCommand = true,
      canConsoleUse = true)
  public void list(ServerPlayer player, int tier) {
    if (tier < 0) {
      ChatHelper.send(player.sender, player.lang.COMMAND_REWARD_INVALID);
      return;
    }
    List<String> items = RewardGenerator.getItemsForTier(tier);
    ChatHelper.send(player.sender, player.lang.MESSAGE_COLOR + Strings.join(items, ", "));
  }
}
