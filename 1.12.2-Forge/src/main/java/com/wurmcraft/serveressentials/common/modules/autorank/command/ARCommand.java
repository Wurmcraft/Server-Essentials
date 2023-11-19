package com.wurmcraft.serveressentials.common.modules.autorank.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.AutoRank;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.autorank.event.RankupEvents;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(
        module = "AutoRank",
        name = "AutoRank",
        defaultAliases = {"Ar"})
public class ARCommand {

    @Command(
            args = {},
            usage = {},
            isSubCommand = false,
            subCommandAliases = {},
            canConsoleUse = false)
    public void check(ServerPlayer player) {
        displayAutoRankRequirements(player.global, player.sender);
        RankupEvents.checkAndHandleUpdate(player.player, player.global);
    }

    @Command(
            args = {CommandArgument.STRING},
            usage = {"uuid"},
            isSubCommand = true,
            subCommandAliases = {"c"},
            canConsoleUse = true)
    public void check(ServerPlayer player, String other) {
        String uuid = PlayerUtils.getUUIDForInput(other);
        if (uuid != null) {
            Account account = PlayerUtils.getLatestAccount(uuid);
            if (account != null) {
                displayAutoRankRequirements(account, player.sender);
            } else {
                ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND.replace("@PLAYER@", other));
            }
        } else {
            ChatHelper.send(player.sender, player.lang.PLAYER_NOT_FOUND.replace("@PLAYER@", other));
        }
    }

    @Command(
            args = {CommandArgument.PLAYER},
            usage = {"player"},
            isSubCommand = true,
            subCommandAliases = {"c"},
            canConsoleUse = true)
    public void check(ServerPlayer player, EntityPlayer otherPlayer) {
        check(player, otherPlayer.getGameProfile().getId().toString());
    }

    @Command(
            args = {CommandArgument.RANK, CommandArgument.RANK, CommandArgument.INTEGER},
            usage = {"rank", "next rank", "playtime"},
            isSubCommand = true,
            subCommandAliases = {"c"},
            canConsoleUse = true)
    public void create(ServerPlayer player, Rank rank, Rank nextRank, int playtime) {
        AutoRank autorank = new AutoRank(rank.name, nextRank.name, playtime, null, 0, "");
        SECore.dataLoader.register(DataType.AUTORANK, autorank.rank, autorank);
        ChatHelper.send(
                player.sender,
                player
                        .lang
                        .COMMAND_AUTORANK_CREATE_BASIC
                        .replaceAll("\\{@RANK@}", rank.name)
                        .replaceAll("\\{@NEXT@}", nextRank.name)
                        .replaceAll("\\{@TIME@}", "" + playtime));
    }

    @Command(
            args = {
                    CommandArgument.RANK,
                    CommandArgument.RANK,
                    CommandArgument.INTEGER,
                    CommandArgument.DOUBLE
            },
            usage = {"rank", "next rank", "playtime", "currency"},
            isSubCommand = true,
            subCommandAliases = {"c"},
            canConsoleUse = true)
    public void create(
            ServerPlayer player, Rank rank, Rank nextRank, int playtime, int currencyAmount) {
        AutoRank autorank =
                new AutoRank(
                        rank.name,
                        nextRank.name,
                        playtime,
                        ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency,
                        currencyAmount,
                        "");
        SECore.dataLoader.register(DataType.AUTORANK, autorank.rank, autorank);
        ChatHelper.send(
                player.sender,
                player
                        .lang
                        .COMMAND_AUTORANK_CREATE
                        .replaceAll("\\{@RANK@}", rank.name)
                        .replaceAll("\\{@NEXT@}", nextRank.name)
                        .replaceAll("\\{@TIME@}", "" + playtime)
                        .replaceAll("\\{@AMOUNT@}", "" + currencyAmount));
    }

    private static void displayAutoRankRequirements(Account account, ICommandSender sender) {
        List<AutoRank> nextRanks = getNextRanks(account);
        Language lang = null;
        if (sender instanceof EntityPlayer) {
            lang = CommandUtils.getPlayerLang((EntityPlayer) sender);
        } else {
            lang =
                    SECore.dataLoader.get(
                            DataType.LANGUAGE,
                            ((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang,
                            new Language());
        }
        for (AutoRank ar : nextRanks) {
            ChatHelper.send(sender, lang.SPACER);
            // Display Playtime
            if (ar.play_time > 0) {
                ChatHelper.send(
                        sender,
                        lang.COMMAND_AR_TIME
                                .replaceAll("\\{@CURRENT_TIME@}", "" + PlayerUtils.getTotalPlaytime(account))
                                .replaceAll("\\{@TIME@}", "" + ar.play_time));
            }
            // Display Required Currency
            if (ar.currency_amount > 0) {
                ChatHelper.send(
                        sender,
                        lang.COMMAND_AR_CURRENCY
                                .replaceAll("\\{@CURRENT_AMOUNT@}", "" + PlayerUtils.getTotalPlaytime(account))
                                .replaceAll("\\{@AMOUNT@}", "" + ar.play_time));
            }
            ChatHelper.send(sender, lang.SPACER);
        }
        if (nextRanks.isEmpty()) {
            ChatHelper.send(sender, lang.COMMAND_AR_MAX);
        }
    }

    private static List<AutoRank> getNextRanks(Account account) {
        List<AutoRank> nextRanks = new ArrayList<>();
        for (AutoRank ar : SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank()).values()) {
            for (String rank : account.rank) {
                if (rank.equals(ar.rank)) {
                    nextRanks.add(ar);
                }
            }
        }
        return nextRanks;
    }
}
