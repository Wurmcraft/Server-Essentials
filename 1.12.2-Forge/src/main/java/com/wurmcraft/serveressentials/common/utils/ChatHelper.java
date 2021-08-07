package com.wurmcraft.serveressentials.common.utils;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.*;

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
        for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            sendTo(player, msg);
    }

    public static void sendFrom(EntityPlayerMP sender, Channel ch, TextComponentString message) {
        HashMap<EntityPlayer, LocalAccount> playersInChannel = getInChannel(ch);
        for (EntityPlayer player : playersInChannel.keySet()) {
            LocalAccount local = playersInChannel.get(player);
            if (!isIgnored(local, sender.getGameProfile().getId().toString()))
                send(player, message);
        }
    }

    public static boolean isIgnored(LocalAccount current, String senderUUID) {
        if (current.ignoredUsers == null || current.ignoredUsers.length == 0)
            return false;
        for (String uuid : current.ignoredUsers)
            if (uuid.equals(senderUUID))
                return true;
        return false;
    }

    public static HashMap<EntityPlayer, LocalAccount> getInChannel(Channel ch) {
        HashMap<EntityPlayer, LocalAccount> players = new HashMap<>();
        for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            LocalAccount local = SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, player.getGameProfile().getId().toString(), new LocalAccount());
            if (ch.name.equals(local.channel))
                players.put(player, local);
        }
        return players;
    }
}
