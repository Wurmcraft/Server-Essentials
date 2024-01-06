package com.wurmcraft.serveressentials.common.modules.economy.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.EcoUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "Economy", name = "Eco")
public class EcoCommand {

  @Command(
      args = {CommandArgument.STRING, CommandArgument.DOUBLE},
      usage = {"Set,Add,Consume", "amount"},
      canConsoleUse = false)
  public void ecoCommand(ServerPlayer player, String type, double amount) {
    if (type.equalsIgnoreCase("Set") || type.equalsIgnoreCase("S")) {
      ecoCommand(
          player,
          "Set",
          player.player,
          amount,
          ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency);
    } else if (type.equalsIgnoreCase("Add") || type.equalsIgnoreCase("A")) {
      ecoCommand(
          player,
          "Add",
          player.player,
          amount,
          ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency);
    } else if (type.equalsIgnoreCase("Consume")
        || type.equalsIgnoreCase("Cons")
        || type.equalsIgnoreCase("C") | type.equalsIgnoreCase("Eat")
        || type.equalsIgnoreCase("E")) {
      ecoCommand(
          player,
          "Consume",
          player.player,
          amount,
          ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency);
    } else {
      ChatHelper.send(player.sender, "Set, Add, Consume");
    }
  }

  @Command(
      args = {CommandArgument.STRING, CommandArgument.PLAYER, CommandArgument.DOUBLE},
      usage = {"Set,Add,Consume", "player", "amount"},
      canConsoleUse = true)
  public void ecoCommand(
      ServerPlayer player, String type, EntityPlayer otherPlayer, double amount) {
    if (type.equalsIgnoreCase("Set") || type.equalsIgnoreCase("S")) {
      ecoCommand(
          player,
          "Set",
          otherPlayer,
          amount,
          ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency);
    } else if (type.equalsIgnoreCase("Add") || type.equalsIgnoreCase("A")) {
      ecoCommand(
          player,
          "Add",
          otherPlayer,
          amount,
          ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency);
    } else if (type.equalsIgnoreCase("Consume")
        || type.equalsIgnoreCase("Cons")
        || type.equalsIgnoreCase("C") | type.equalsIgnoreCase("Eat")
        || type.equalsIgnoreCase("E")) {
      ecoCommand(
          player,
          "Consume",
          otherPlayer,
          amount,
          ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency);
    } else {
      ChatHelper.send(player.sender, "Set, Add, Consume");
    }
  }

  @Command(
      args = {
        CommandArgument.STRING,
        CommandArgument.PLAYER,
        CommandArgument.DOUBLE,
        CommandArgument.CURRENCY
      },
      usage = {"Set,Add,Consume", "player", "amount", "currency"},
      canConsoleUse = true)
  public void ecoCommand(
      ServerPlayer player, String type, EntityPlayer otherPlayer, double amount, String currency) {
    if (type.equalsIgnoreCase("Set") || type.equalsIgnoreCase("S")) {
      Account otherAccount =
          PlayerUtils.getLatestAccount(otherPlayer.getGameProfile().getId().toString());
      if (otherAccount != null) {
        EcoUtils.set(otherAccount, currency, amount);
        // Display
        if (player.player.getGameProfile().getId().toString().equals(otherAccount.uuid)) {
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_ECO_SET
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency));
        } else {
          ChatHelper.send(
              otherPlayer,
              SECore.dataLoader
                  .get(DataLoader.DataType.LANGUAGE, otherAccount.lang, new Language())
                  .COMMAND_ECO_SET
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency));
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_ECO_SET_OTHER
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency)
                  .replaceAll("\\{@PLAYER@\\}", ChatHelper.getName(otherPlayer, otherAccount)));
        }
      }
    } else if (type.equalsIgnoreCase("Add") || type.equalsIgnoreCase("A")) {
      Account otherAccount =
          PlayerUtils.getLatestAccount(otherPlayer.getGameProfile().getId().toString());
      if (otherAccount != null) {
        EcoUtils.earn(otherAccount, currency, amount);
        // Display
        if (player.player.getGameProfile().getId().toString().equals(otherAccount.uuid)) {
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_ECO_ADD
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency));
        } else {
          ChatHelper.send(
              otherPlayer,
              SECore.dataLoader
                  .get(DataLoader.DataType.LANGUAGE, otherAccount.lang, new Language())
                  .COMMAND_ECO_SET
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency));
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_ECO_ADD_OTHER
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency)
                  .replaceAll("\\{@PLAYER@\\}", ChatHelper.getName(otherPlayer, otherAccount)));
        }
      }
    } else if (type.equalsIgnoreCase("Consume")
        || type.equalsIgnoreCase("Cons")
        || type.equalsIgnoreCase("C") | type.equalsIgnoreCase("Eat")
        || type.equalsIgnoreCase("E")) {
      Account otherAccount =
          PlayerUtils.getLatestAccount(otherPlayer.getGameProfile().getId().toString());
      if (otherAccount != null) {
        EcoUtils.buy(otherAccount, currency, amount);
        // Display
        if (player.player.getGameProfile().getId().toString().equals(otherAccount.uuid)) {
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_ECO_CONSUME
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency));
        } else {
          ChatHelper.send(
              otherPlayer,
              SECore.dataLoader
                  .get(DataLoader.DataType.LANGUAGE, otherAccount.lang, new Language())
                  .COMMAND_ECO_SET
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency));
          ChatHelper.send(
              player.sender,
              player
                  .lang
                  .COMMAND_ECO_CONSUME_OTHER
                  .replaceAll("\\{@AMOUNT@\\}", String.format("%.2f", amount))
                  .replaceAll("\\{@CURRENCY@\\}", currency)
                  .replaceAll("\\{@PLAYER@\\}", ChatHelper.getName(otherPlayer, otherAccount)));
        }
      }
    } else {
      ChatHelper.send(player.sender, "Set, Add, Consume");
    }
  }
}
