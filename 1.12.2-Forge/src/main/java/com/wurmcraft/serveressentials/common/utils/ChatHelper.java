package com.wurmcraft.serveressentials.common.utils;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.*;
import com.wurmcraft.serveressentials.api.models.data_wrapper.ChatMessage;
import com.wurmcraft.serveressentials.api.models.local.Bulletin;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.ws.SocketController;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.wurmcraft.serveressentials.ServerEssentials.GSON;
import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

public class ChatHelper {

    public static NonBlockingHashMap<String, String> lastMessageCache = new NonBlockingHashMap<>();
    public static List<EntityPlayer> socialSpy = new ArrayList<>();

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
        LOG.info("[Broadcast]: " + msg);
    }

    public static void sendFrom(EntityPlayerMP sender, Channel ch, TextComponentString message) {
        HashMap<EntityPlayer, LocalAccount> playersInChannel = getInChannel(ch);
        for (EntityPlayer player : playersInChannel.keySet()) {
            LocalAccount local = playersInChannel.get(player);
            boolean ignored = isIgnored(local, sender.getGameProfile().getId().toString());
            if (!ignored || RankUtils.hasPermission(SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, local.uuid, new Account()), "chat.ignore.bypass"))
                send(player, message);
        }
        LOG.info("[Chat]: " + message.getFormattedText());
        try {
            SocketController.send(new WSWrapper(200, WSWrapper.Type.MESSAGE, new DataWrapper("chat", GSON.toJson(new ChatMessage("Minecraft", ServerEssentials.config.general.serverID, getName(sender, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, sender.getGameProfile().getId().toString(), new Account())), message.getText(), ch.name)))));
        } catch (Exception e) {
            LOG.warn("Failed to send message via webSocket!");
            e.printStackTrace();
        }
    }

    public static void send(EntityPlayer sender, EntityPlayer receiver, String msg) {
        if (isIgnored(SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, receiver.getGameProfile().getId().toString(), new LocalAccount()), sender.getGameProfile().getId().toString())) {
            return;
        }
        String msgColor = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, ((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang, new Language()).MESSAGE_COLOR;
        String format = replaceColor(msgColor + ((ConfigChat) SECore.moduleConfigs.get("CHAT")).messageFormat.replaceAll("%MSG%", msg));
        String dir = format.substring(format.indexOf("{") + 1, format.indexOf("}"));
        String[] split = dir.split(",");
        format = format.substring(0, format.indexOf("{")) + " {REPLACE} " + format.substring(format.indexOf("}") + 1);
        send(receiver, format.replace("{REPLACE}", split[0].trim()).replaceAll("%NAME%", getName(sender, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, sender.getGameProfile().getId().toString(), new Account())).trim()).replaceAll("%USERNAME%", sender.getDisplayNameString()));
        String sentMessage = format.replace("{REPLACE}", split[1].trim()).replaceAll("%NAME%", getName(receiver, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, receiver.getGameProfile().getId().toString(), new Account())).trim()).replaceAll("%USERNAME%", receiver.getDisplayNameString());
        send(sender, sentMessage);
        lastMessageCache.put(receiver.getGameProfile().getId().toString(), sender.getGameProfile().getId().toString());
        if (socialSpy.size() > 0) {
            for (EntityPlayer spy : ChatHelper.socialSpy) {
                if (spy.getGameProfile().getId().toString().equals(sender.getGameProfile().getId().toString()) || spy.getGameProfile().getId().toString().equals(receiver.getGameProfile().getId().toString()))
                    continue;
                Language lang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, spy.getGameProfile().getId().toString(), new Account()).language, new Language());
                send(spy, lang.SOCIAL_SPY_TAG + " " + msgColor + sender.getDisplayNameString() + " " + sentMessage);
            }
        }
        LOG.info("[SocialSpy]: " + ChatHelper.replaceColor(sender.getDisplayNameString() + " " + sentMessage));
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

    public static void send(EntityPlayer player, Bulletin bulletin) {
        Language lang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, player.getGameProfile().getId().toString(), new Account()).language, new Language());
        send(player, lang.SPACER);
        send(player, bulletin.title);         // TODO Center Title
        send(player, bulletin.message);
        send(player, lang.SPACER);
    }
}
