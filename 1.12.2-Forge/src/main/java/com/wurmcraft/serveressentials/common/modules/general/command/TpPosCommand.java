package com.wurmcraft.serveressentials.common.modules.general.command;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import com.wurmcraft.serveressentials.common.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

@ModuleCommand(module = "General", name = "TpPos", defaultAliases = {"tpp"})
public class TpPosCommand {

    @Command(args = {CommandArgument.INTEGER, CommandArgument.INTEGER}, usage = {"x", "z"})
    public void teleportPos(ServerPlayer player, int x, int z) {
        teleportPos(player, x, WorldUtils.findTop(player.player.world, x, z), z);
    }

    @Command(args = {CommandArgument.INTEGER, CommandArgument.INTEGER, CommandArgument.INTEGER}, usage = {"x", "y", "z"})
    public void teleportPos(ServerPlayer player, int x, int y, int z) {
        teleportPos(player, x, y, z, player.player.dimension);
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.INTEGER, CommandArgument.INTEGER, CommandArgument.INTEGER}, usage = {"player", "x", "y", "z", "dim"})
    public void teleportPos(ServerPlayer player, int x, int y, int z, int dim) {
        Location location = new Location(x, y, z, dim, player.player.rotationPitch, player.player.rotationYaw);
        if (TeleportUtils.teleportTo((EntityPlayerMP) player.player, player.local, location, RankUtils.hasPermission(player.global, "command.tppos.bypass")))
            ChatHelper.send(player.player, player.lang.COMMAND_TPPOS);
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.INTEGER, CommandArgument.INTEGER}, usage = {"player", "x", "z"}, canConsoleUse = true)
    public void teleportOtherPos(ServerPlayer player, EntityPlayer otherPlayer, int x, int z) {
        teleportOtherPos(player, otherPlayer, x, WorldUtils.findTop(otherPlayer.world, x, z), z);
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.INTEGER, CommandArgument.INTEGER, CommandArgument.INTEGER}, usage = {"player", "x", "y", "z"}, canConsoleUse = true)
    public void teleportOtherPos(ServerPlayer player, EntityPlayer otherPlayer, int x, int y, int z) {
        teleportOtherPos(player, otherPlayer, x, y, z, otherPlayer.dimension);
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.INTEGER, CommandArgument.INTEGER, CommandArgument.INTEGER}, usage = {"player", "x", "y", "z", "dim"}, canConsoleUse = true)
    public void teleportOtherPos(ServerPlayer player, EntityPlayer otherPlayer, int x, int y, int z, int dim) {
        if (RankUtils.hasPermission(player.global, "command.tppos.other")) {
            Location location = new Location(x, y, z, dim, otherPlayer.rotationPitch, otherPlayer.rotationYaw);
            if (TeleportUtils.teleportTo((EntityPlayerMP) otherPlayer, SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, otherPlayer.getGameProfile().getId().toString(), new LocalAccount()), location, RankUtils.hasPermission(player.global, "command.tppos.bypass"))) {
                Language otherLang = CommandUtils.getPlayerLang(otherPlayer);
                ChatHelper.send(otherPlayer, otherLang.COMMAND_TPPOS);
                ChatHelper.send(player.sender, player.lang.COMMAND_TPPOS_OTHER.replaceAll("\\{@PLAYER@}", otherPlayer.getDisplayNameString()));
            }
        } else
            ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }
}
