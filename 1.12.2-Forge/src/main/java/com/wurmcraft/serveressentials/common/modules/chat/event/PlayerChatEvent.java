package com.wurmcraft.serveressentials.common.modules.chat.event;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class PlayerChatEvent {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatEvent(ServerChatEvent e) {
        LocalAccount local = SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, e.getPlayer().getGameProfile().getId().toString(), new LocalAccount());
        Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, e.getPlayer().getGameProfile().getId().toString(), new Account());
        if (isMuted(account)) {
            // TODO Send muted message
            e.setCanceled(true);
            return;
        }
        Channel ch = SECore.dataLoader.get(DataLoader.DataType.CHANNEL, local.channel, new Channel());
        e.setComponent(new TextComponentString(format(e.getPlayer(), ch, account, e.getMessage())));
    }

    private boolean isMuted(Account account) {
        return account.muted;
    }

    public static String format(EntityPlayer player, Channel channel, Account account, String msg) {
        String format = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).chatFormat;
        format =  format.replaceAll("%NAME%", player.getDisplayNameString()).replaceAll("%CHANNEL_PREFIX%", channel.prefix).replaceAll("%MESSAGE%", msg).replaceAll("%RANK_PREFIX%", getRankPrefix(account.rank)).replaceAll("%RANK_SUFFIX%", getRankSuffix(account.rank));
        // TODO Color Code filtering only, regex
        return format.replaceAll("&", "\u00A7");
    }

    public static String getRankPrefix(String[] ranks) {
        List<Rank> list = new ArrayList<>();
        for (String rank : ranks)
            list.add(SECore.dataLoader.get(DataLoader.DataType.RANK, rank, new Rank()));
        Rank highestRankPriority = null;
        int priority = Integer.MIN_VALUE;
        for (Rank rank : list)
            if (rank != null)
                if (rank.prefixPriority > priority) {
                    priority = rank.prefixPriority;
                    highestRankPriority = rank;
                }
        if (highestRankPriority != null)
            return highestRankPriority.prefix;
        return "";
    }

    public static String getRankSuffix(String[] ranks) {
        List<Rank> list = new ArrayList<>();
        for (String rank : ranks)
            list.add(SECore.dataLoader.get(DataLoader.DataType.RANK, rank, new Rank()));
        Rank highestRankPriority = null;
        int priority = Integer.MIN_VALUE;
        for (Rank rank : list)
            if (rank != null)
                if (rank.suffixPriority > priority) {
                    priority = rank.suffixPriority;
                    highestRankPriority = rank;
                }
        if (highestRankPriority != null)
            return highestRankPriority.suffix;
        return "";
    }
}
