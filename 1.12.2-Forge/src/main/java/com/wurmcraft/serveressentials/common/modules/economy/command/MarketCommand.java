package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.general.utils.inventory.ShopInventory;

// TODO Implement
@ModuleCommand(module = "Economy", name = "Market", defaultAliases = {"Shop"})
public class MarketCommand {

  @Command(args = {}, usage = {})
  public void localMarket(ServerPlayer player) {
    globalMarket(player, false, 0, "*");
  }

  @Command(args = {CommandArgument.INTEGER}, usage = {"page"})
  public void localMarket(ServerPlayer player, int page) {
    globalMarket(player, false, page, "*");
  }

  @Command(args = {CommandArgument.STRING}, usage = {"filter"})
  public void localMarket(ServerPlayer player, String filter) {
    globalMarket(player, false, 0, filter);
  }

  @Command(args = {CommandArgument.INTEGER, CommandArgument.STRING}, usage = {"page",
      "item"})
  public void localMarket(ServerPlayer player, int page, String filter) {
    globalMarket(player, false, page, filter);
  }

  @Command(args = {CommandArgument.STRING}, usage = {"item"})
  public void globalMarket(ServerPlayer player, boolean global) {
    globalMarket(player, global, 0, "*");
  }

  @Command(args = {CommandArgument.STRING}, usage = {"item"})
  public void globalMarket(ServerPlayer player, boolean global, int page) {
    globalMarket(player, global, page, "*");
  }

  @Command(args = {CommandArgument.STRING}, usage = {"item"})
  public void globalMarket(ServerPlayer player, boolean global, String filter) {
    globalMarket(player, global, 0, filter);
  }

  @Command(args = {CommandArgument.STRING}, usage = {"item"})
  public void globalMarket(ServerPlayer player, boolean global, int page, String item) {
    player.player.displayGUIChest(
        new ShopInventory(player.player, player.lang, page, global, item));
  }

}
