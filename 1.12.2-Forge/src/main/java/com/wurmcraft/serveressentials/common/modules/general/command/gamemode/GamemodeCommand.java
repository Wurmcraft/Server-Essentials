package com.wurmcraft.serveressentials.common.modules.general.command.gamemode;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

@ModuleCommand(module = "General", name = "GameMode", defaultAliases = {"Gm", "Mode"})
public class GamemodeCommand {

    @Command(args = {CommandArgument.STRING}, usage = {"survival, creative, adventure, spectator"})
    public void gamemode(ServerPlayer player, String type) {
        if (type.equalsIgnoreCase("survival") || type.toLowerCase().startsWith("s") || type.equalsIgnoreCase("0"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/survival");
        else if (type.equalsIgnoreCase("creative") || type.toLowerCase().startsWith("c") || type.equalsIgnoreCase("1"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/creative");
        else if (type.equalsIgnoreCase("adventure") || type.toLowerCase().startsWith("a") || type.equalsIgnoreCase("2"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/adventure");
        else if (type.equalsIgnoreCase("spectator") || type.toLowerCase().startsWith("sp") || type.equalsIgnoreCase("3"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/spectator");
    }

    @Command(args = {CommandArgument.PLAYER, CommandArgument.STRING}, usage = {"player", "survival, creative, adventure, spectator"})
    public void gamemodeOther(ServerPlayer player, EntityPlayer otherPlayer, String type) {
        if (type.equalsIgnoreCase("survival") || type.toLowerCase().startsWith("s") || type.equalsIgnoreCase("0"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/survival " + otherPlayer.getGameProfile().getName());
        else if (type.equalsIgnoreCase("creative") || type.toLowerCase().startsWith("c") || type.equalsIgnoreCase("1"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/creative " + otherPlayer.getGameProfile().getName());
        else if (type.equalsIgnoreCase("adventure") || type.toLowerCase().startsWith("a") || type.equalsIgnoreCase("2"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/adventure " + otherPlayer.getGameProfile().getName());
        else if (type.equalsIgnoreCase("spectator") || type.toLowerCase().startsWith("sp") || type.equalsIgnoreCase("3"))
            FMLCommonHandler.instance().getMinecraftServerInstance().commandManager.executeCommand(player.sender, "/spectator " + otherPlayer.getGameProfile().getName());
    }
}
