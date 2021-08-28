package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.Warp;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "Warp")
public class WarpCommand {

    @Command(args = {CommandArgument.WARP}, usage = {"name"})
    public void warp(ServerPlayer player, Warp warp) {
        if (TeleportUtils.teleportTo((EntityPlayerMP) player.player, player.local, warp) && RankUtils.hasPermission(player.global, "command.warp." + warp.name))
            ChatHelper.send(player.sender, player.lang.COMMAND_WARP.replaceAll("\\{@NAME@}", warp.name));
    }

    @Command(args = {}, usage = {}, isSubCommand = true, subCommandAliases = {"l"}, canConsoleUse = true)
    public void list(ServerPlayer player) {
        try {
            Warp[] warps = SECore.dataLoader.getFromKey(DataLoader.DataType.WARP, new Warp[0]).values().toArray(new Warp[0]);
            StringBuilder builder = new StringBuilder();
            for (Warp warp : warps)
                if (RankUtils.hasPermission(player.global, "command.warp." + warp.name))
                    builder.append(warp.name).append(", ");
            ChatHelper.send(player.sender, player.lang.COMMAND_WARP_LIST.replaceAll("\\{@LIST@}", builder.substring(0, builder.length() - 2)));
        } catch (Exception e) {
            ChatHelper.send(player.sender, player.lang.COMMAND_WARP_LIST.replaceAll("\\{@LIST@}", "[]"));
        }
    }

    @Command(args = {CommandArgument.STRING}, usage = {"name"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {"c"})
    public void create(ServerPlayer player, String name) {
        FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/setwarp " + name);
    }

    @Command(args = {CommandArgument.STRING}, usage = {"name"}, isSubCommand = true, canConsoleUse = true, subCommandAliases = {"del", "d", "remove", "rem", "r"})
    public void delete(ServerPlayer player, String name) {
        FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/deleteWarp " + name);
    }
}
