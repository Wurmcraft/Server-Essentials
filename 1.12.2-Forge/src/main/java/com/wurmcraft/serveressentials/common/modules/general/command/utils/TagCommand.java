package com.wurmcraft.serveressentials.common.modules.general.command.utils;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;

@ModuleCommand(module = "General", name = "Tag")
public class TagCommand {

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING}, usage = {"player", "tag"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {"A"})
    public void add(ServerPlayer sender, EntityPlayer player, String tag) {
        player.addTag(tag);
        ChatHelper.send(sender.sender, sender.lang.COMMAND_TAG_ADD.replaceAll("\\{@TAG@}", tag).replaceAll("\\{@PLAYER@}", player.getName()));
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING}, usage = {"player", "tag"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {"Rem", "R", "Delete", "Del", "D"})
    public void remove(ServerPlayer sender, EntityPlayer player, String tag) {
        player.removeTag(tag);
        ChatHelper.send(sender.sender, sender.lang.COMMAND_TAG_REM.replaceAll("\\{@TAG@}", tag).replaceAll("\\{@PLAYER@}", player.getName()));
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING}, usage = {"player", "tag"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {"l"})
    public void list(ServerPlayer sender, EntityPlayer player) {
        ChatHelper.send(sender.sender, sender.lang.COMMAND_TAG_LIST.replaceAll("\\{@LIST@}", Strings.join(player.getTags(), ",")));
    }
}
