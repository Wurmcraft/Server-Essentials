package com.wurmcraft.serveressentials.common.modules.general.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Spawn", defaultAliases = {"Spwn", "S"})
public class SpawnCommand {

    @Command(args = {}, usage = {})
    public void spawn(ServerPlayer player) {
        Location spawn = PlayerUtils.getSpawn(player.global.rank);
        TeleportUtils.teleportTo((EntityPlayerMP) player.player, player.local, spawn);
        ChatHelper.send(player.sender, player.lang.COMMAND_SPAWN);
    }

    @Command(args = {CommandArgument.STRING, CommandArgument.STRING}, usage = {"set", "rank"}, isSubCommand = true, subCommandAliases = {"s"})
    public void setSpawn(ServerPlayer player, String arg, String rank) {
        if (RankUtils.hasPermission(player.global, "command.setspawn")) {
            if (arg.equalsIgnoreCase("set"))
                FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/setSpawn " + rank);
        } else
            ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
    }

    @Command(args = {CommandArgument.STRING}, usage = {"rank"})
    public void spawnSpecific(ServerPlayer player, String arg) {
        if (!arg.equalsIgnoreCase("set")) {
            if (RankUtils.hasPermission(player.global, "command.spawn." + arg.toLowerCase())) {
                Location spawn = PlayerUtils.getSpawn(new String[]{arg});
                if (spawn != null) {
                    TeleportUtils.teleportTo((EntityPlayerMP) player.player, player.local, spawn);
                    ChatHelper.send(player.sender, player.lang.COMMAND_SPAWN);
                }
            } else
                ChatHelper.send(player.sender, new TextComponentTranslation("commands.generic.permission"));
        } else
            setSpawn(player, "set", "*");
    }
}
