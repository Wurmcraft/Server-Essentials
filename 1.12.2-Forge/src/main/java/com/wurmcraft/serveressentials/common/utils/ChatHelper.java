package com.wurmcraft.serveressentials.common.utils;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.*;

public class ChatHelper {

    public static NonBlockingHashMap<UUID, UUID> lastMessageCache = new NonBlockingHashMap<>();

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
            boolean ignored = isIgnored(local, sender.getGameProfile().getId().toString());
            if (!ignored || RankUtils.hasPermission(SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, local.uuid, new Account()), "chat.ignore.bypass"))
                send(player, message);
        }
    }

    public static void send(EntityPlayer sender, EntityPlayer receiver, String msg) {
        if (isIgnored(SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, receiver.getGameProfile().getId().toString(), new LocalAccount()), sender.getGameProfile().getId().toString())) {
            return;
        }
        String format = replaceColor(SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, ((ConfigCore)SECore.moduleConfigs.get("CORE")).defaultLang, new Language()).MESSAGE_COLOR + ((ConfigChat) SECore.moduleConfigs.get("CHAT")).messageFormat.replaceAll("%MSG%", msg));
        String dir = format.substring(format.indexOf("{") + 1, format.indexOf("}"));
        String[] split = dir.split(",");
        format = format.substring(0, format.indexOf("{")) + " {REPLACE} " + format.substring(format.indexOf("}")+1);
        send(receiver, new TextComponentString(format.replace("{REPLACE}", split[0].trim()).replaceAll("%NAME%", getName(sender, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, sender.getGameProfile().getId().toString(), new Account())).trim()).replaceAll("%USERNAME%", sender.getDisplayNameString())));
        send(sender, new TextComponentString(format.replace("{REPLACE}", split[1].trim()).replaceAll("%NAME%", getName(receiver, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, receiver.getGameProfile().getId().toString(), new Account())).trim()).replaceAll("%USERNAME%", receiver.getDisplayNameString())));
        lastMessageCache.put(receiver.getGameProfile().getId(), sender.getGameProfile().getId());
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

    public static String getName(EntityPlayer player, Account account) {
        if (account.displayName == null || account.displayName.isEmpty())
            return player.getDisplayNameString();
        else
            return ((ConfigChat) SECore.moduleConfigs.get("CHAT")).nickFormat.replaceAll("%NICK%", account.displayName).replaceAll("%USERNAME%", player.getDisplayNameString());
    }
}
