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

@ModuleCommand(module = "General", name = "God", defaultAliases = "Invincible")
public class GodCommand {

    @Command(args = {}, usage = {})
    public void selfGod(ServerPlayer player) {
        if (player.player.capabilities.disableDamage) {
            player.player.capabilities.disableDamage = false;
            ChatHelper.send(player.sender, player.lang.COMMAND_GOD_OFF);
        } else {
            player.player.capabilities.disableDamage = true;
            ChatHelper.send(player.sender, player.lang.COMMAND_GOD_ON);
        }
    }

    @Command(args = {CommandArgument.PLAYER}, usage = {"player"})
    public void godOther(ServerPlayer player, EntityPlayer otherPlayer) {
        if (RankUtils.hasPermission(player.global, "command.god.other")) {
            if (otherPlayer.capabilities.disableDamage) {
                otherPlayer.capabilities.disableDamage = false;
                Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
                ChatHelper.send(otherPlayer, otherLang.COMMAND_GOD_ON);
                ChatHelper.send(player.sender, player.lang.COMMAND_GOD_OFF_OTHER);
            } else {
                otherPlayer.capabilities.disableDamage = true;
                Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
                ChatHelper.send(otherPlayer, otherLang.COMMAND_GOD_ON);
                ChatHelper.send(player.sender, player.lang.COMMAND_GOD_ON_OTHER);
            }
        } else
            ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
}
