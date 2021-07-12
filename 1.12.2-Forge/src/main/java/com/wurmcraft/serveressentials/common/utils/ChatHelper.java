package com.wurmcraft.serveressentials.common.utils;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatHelper {

    public static void send(ICommandSender sender, ITextComponent component) {
        sender.sendMessage(component);
    }

    public static void send(ICommandSender sender, String message) {
        // TODO Fix '&' replacement for valid color codes only
        send(sender, new TextComponentString(message.replaceAll("&", "\u00a7")));
    }
}
