package com.wurmcraft.serveressentials.common.modules.transfer.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.TransferEntry;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.general.utils.inventory.TransferInventory;
import com.wurmcraft.serveressentials.common.modules.transfer.ConfigTransfer;
import com.wurmcraft.serveressentials.common.modules.transfer.utils.TransferUtils;

@ModuleCommand(module = "Transfer", name = "Transfer", defaultAliases = {"tsfr", "tfr", "tf"})
public class TransferCommand {

    public static final String TRANSFER_ID = ((ConfigTransfer) SECore.moduleConfigs.get("TRANSFER")).transferID;

    @Command(args = {CommandArgument.STRING}, usage = {"all,hand,hotbar"}, isSubCommand = true, subCommandAliases = {"s"})
    public static void send(ServerPlayer sender, String type) {
        // TODO Implement
    }

    @Command(args = {}, usage = {}, isSubCommand = true, subCommandAliases = {"g"})
    public static void get(ServerPlayer sender) {
        get(sender, 0);
    }

    @Command(args = {CommandArgument.INTEGER}, usage = {"page"}, isSubCommand = true, subCommandAliases = {"g"})
    public static void get(ServerPlayer sender, int page) {
        TransferEntry[] entry = TransferUtils.get(TRANSFER_ID, sender.player.getGameProfile().getId().toString());
        if (entry != null && entry.length == 1) {
            if(!entry[0].is_currently_open) {
//    TODO            sender.player.displayGUIChest(new TransferInventory(sender.player, sender.lang, entry[0], page));
            }
        } else {
            // TODO No Entry or corrupted
//            ChatHelper.send();
        }
    }
}
