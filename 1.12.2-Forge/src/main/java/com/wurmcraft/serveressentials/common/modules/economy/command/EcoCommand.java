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
            args = {CommandArgument.DOUBLE},
            usage = {"amount"},
            isSubCommand = true,
            subCommandAliases = "S",
            canConsoleUse = false)
    public void Set(ServerPlayer player, double amount) {
        Set(player, player.player, amount);
    }

    @Command(
            args = {CommandArgument.PLAYER, CommandArgument.DOUBLE},
            usage = {"player", "amount"},
            isSubCommand = true,
            subCommandAliases = "S",
            canConsoleUse = true)
    public void Set(ServerPlayer player, EntityPlayer otherPlayer, double amount) {
        Set(
                player,
                player.player,
                ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency,
                amount);
    }

    @Command(
            args = {CommandArgument.PLAYER, CommandArgument.CURRENCY, CommandArgument.DOUBLE},
            usage = {"player", "currency", "amount"},
            isSubCommand = true,
            subCommandAliases = "S",
            canConsoleUse = true)
    public void Set(ServerPlayer player, EntityPlayer otherPlayer, String currency, double amount) {
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
    }

    @Command(
            args = {CommandArgument.DOUBLE},
            usage = {"amount"},
            isSubCommand = true,
            subCommandAliases = "A",
            canConsoleUse = false)
    public void Add(ServerPlayer player, double amount) {
        Add(player, player.player, amount);
    }

    @Command(
            args = {CommandArgument.PLAYER, CommandArgument.DOUBLE},
            usage = {"player", "amount"},
            isSubCommand = true,
            subCommandAliases = "A",
            canConsoleUse = true)
    public void Add(ServerPlayer player, EntityPlayer otherPlayer, double amount) {
        Add(
                player,
                otherPlayer,
                ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency,
                amount);
    }

    @Command(
            args = {CommandArgument.PLAYER, CommandArgument.CURRENCY, CommandArgument.DOUBLE},
            usage = {"player", "currency", "amount"},
            isSubCommand = true,
            subCommandAliases = "A",
            canConsoleUse = true)
    public void Add(ServerPlayer player, EntityPlayer otherPlayer, String currency, double amount) {
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
    }

    @Command(
            args = {CommandArgument.DOUBLE},
            usage = {"amount"},
            isSubCommand = true,
            subCommandAliases = {"C", "Con", "Eat", "E"},
            canConsoleUse = false)
    public void consume(ServerPlayer player, double amount) {
        consumeP(player, player.player, amount);
    }

    @Command(
            args = {CommandArgument.PLAYER, CommandArgument.DOUBLE},
            usage = {"player", "amount"},
            isSubCommand = true,
            subCommandAliases = {"C", "Con", "Eat", "E"},
            canConsoleUse = true)
    public void consumeP(ServerPlayer player, EntityPlayer otherPlayer, double amount) {
        consumeC(
                player,
                otherPlayer,
                amount,
                ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency);
    }

    @Command(
            args = {CommandArgument.PLAYER, CommandArgument.DOUBLE, CommandArgument.CURRENCY},
            usage = {"player", "amount", "currency"},
            isSubCommand = true,
            subCommandAliases = {"C", "Con", "Eat", "E"},
            canConsoleUse = true)
    public void consumeC(
            ServerPlayer player, EntityPlayer otherPlayer, double amount, String currency) {
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
    }
}
