package com.wurmcraft.serveressentials.common.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.regex.Pattern;

public class ChatHelper {

    public static void send(ICommandSender sender, ITextComponent component) {
        sender.sendMessage(component);
    }

    public static void send(ICommandSender sender, String message) {
        send(sender, new TextComponentString(replaceColor(message)));
    }

    public static String replaceColor(String message) {
       return message.replaceAll("[&]([0-9A-Fa-fK-ORk-or])", "\u00a7$1");
    }

    public static void sendTo(ICommandSender sender, String message) {
        send(sender, message);
    }

    public static void sendToAll(String msg) {
        for(EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            sendTo(player, msg);
    }
}
