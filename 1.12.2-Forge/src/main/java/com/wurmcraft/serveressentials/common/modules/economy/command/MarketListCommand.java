package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.MarketEntry;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.transfer.ItemWrapper;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.economy.MarketHelper;
import com.wurmcraft.serveressentials.common.modules.transfer.ConfigTransfer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.time.Instant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

// TODO Global Listing
@ModuleCommand(module = "Economy", name = "MarketList", defaultAliases = {
    "MList"}, defaultCooldown = "*:5")
public class MarketListCommand {

  @Command(args = {CommandArgument.DOUBLE}, usage = "Cost")
  public void listItem(ServerPlayer player, double cost) {
    listItem(player, cost, player.player.getActiveItemStack().getCount());
  }

  @Command(args = {CommandArgument.DOUBLE, CommandArgument.INTEGER}, usage = {"Cost",
      "Count Per Listing"})
  public void listItem(ServerPlayer player, double cost, int perListing) {
    if (hasValidItem(player.player)) {
      ItemStack itemInQuestion = player.player.getActiveItemStack();
      int listingsToCreate = itemInQuestion.getCount() / perListing;
      if (listingsToCreate > 0) {
        ItemWrapper item = new ItemWrapper(
            ServerEssentials.stackConverter.toString(itemInQuestion));
        MarketEntry marketListing = new MarketEntry(
            ServerEssentials.config.general.serverID,
            player.player.getGameProfile().getId().toString(), item,
            ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency, cost,
            Instant.EPOCH.toEpochMilli(), "Selling", "{}",
            ((ConfigTransfer) SECore.moduleConfigs.get("TRANSFER")).transferID);
        for (int count = 0; count < listingsToCreate; count++) {
          MarketHelper.entries.add(marketListing);
        }
        MarketHelper.saveLocal();
        ChatHelper.send(player.sender,
            player.lang.COMMAND_MARKETLIST_LISTED.replaceAll("\\{@ITEM@}",
                    ServerEssentials.stackConverter.toString(itemInQuestion))
                .replaceAll("\\{@PRICE@}", String.format("%.2f", cost)
                    .replaceAll("\\{@COUNT@}", "" + perListing)));
        player.player.getActiveItemStack()
            .setCount(itemInQuestion.getCount() - (listingsToCreate * perListing));
      } else {
        ChatHelper.send(player.sender,
            player.lang.COMMAND_MARKETLIST_INVALID_ITEM.replaceAll("\\{@ITEM@}",
                ServerEssentials.stackConverter.toString(
                    player.player.getActiveItemStack())));
      }
    } else {
      ChatHelper.send(player.sender,
          player.lang.COMMAND_MARKETLIST_INVALID_ITEM.replaceAll("\\{@ITEM@}",
              ServerEssentials.stackConverter.toString(
                  player.player.getActiveItemStack())));
    }
  }

  private static boolean hasValidItem(EntityPlayer player) {
    return false;
  }
}
