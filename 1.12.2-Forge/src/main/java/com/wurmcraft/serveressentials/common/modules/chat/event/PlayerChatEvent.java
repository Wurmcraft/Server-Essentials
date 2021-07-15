package com.wurmcraft.serveressentials.common.modules.chat.event;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.EcoUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;
import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;

public class PlayerChatEvent {
    public static final String[] REPLACE_LIST = new String[]{
            "%USERNAME%",
            "%NAME%",
            "%DIMENSION%",
            "%RANK_PREFIX%",
            "%RANK_SUFFIX%",
            "%CHANNEL_PREFIX%"
    };

    public static final String[] PLAYER_REPLACEMENT = new String[]{
            "{BALANCE}",
            "{EXP}",
            "{LEVEL}",
            "{PLAY_TIME}",
            "{TIME}",
            "{REWARDS}",
            "{POINTS}",
            "{LANGUAGE}",
            "{LANG}"
    };

    public static SimpleDateFormat log_format = new SimpleDateFormat("dd/MM/yy HH:mm:ss zzz");

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
        TextComponentString message = new TextComponentString(format(e.getPlayer(), ch, account, e.getMessage()));
        e.setComponent(message);
        if (ch.logChat)
            logChat(ch, message.getText());
    }

    private void logChat(Channel ch, String message) {
        message = log_format.format(new Date()) + " " + message + "\n";
        File logFile = new File(SAVE_DIR + File.separator + "Logs" + File.separator + "channel" + File.separator + ch.name + ".log");
        try {
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
            Files.write(logFile.toPath(), message.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("Failed to write chat log for channel '" + ch.name + "'");
        }
    }

    private boolean isMuted(Account account) {
        return account.muted; // TODO Check for temp mute
    }

    public static String format(EntityPlayer player, Channel channel, Account account, String msg) {
        String format = ChatHelper.replaceColor(((ConfigChat) SECore.moduleConfigs.get("CHAT")).chatFormat);
        List<Rank> ranks = getRanks(account);
        format = StringUtils.replaceEach(format, REPLACE_LIST, new String[]{
                ChatHelper.replaceColor(getRankValue("color", ranks)) + player.getGameProfile().getName(),
                ChatHelper.replaceColor(getRankValue("color", ranks)) + getName(player, account),
                player.dimension + "",
                ChatHelper.replaceColor(getRankValue("prefix", ranks)),
                ChatHelper.replaceColor(getRankValue("suffix", ranks)),
                ChatHelper.replaceColor(channel.prefix)
        });
        // Channel Specific
        if (channel.chatReplacment != null && channel.chatReplacment.size() > 0)
            for (String regex : channel.chatReplacment.keySet())
                msg = msg.replaceAll(regex, ChatHelper.replaceColor(channel.chatReplacment.get(regex)));
        // Player Replacement
        if (RankUtils.hasPermission(account, "chat.replacement")) {
            msg = StringUtils.replaceEach(msg, PLAYER_REPLACEMENT, new String[]{
                    String.format("%.2f", EcoUtils.balance(account, "default")), // TODO Default currency config
                    String.format("%.4f", player.experience),
                    player.experienceLevel + "",
                    "0d",       // TODO Get Total Playtime
                    "0d",       // TODO Get Total Playtime
                    account.rewardPoints + "",
                    account.rewardPoints + "",
                    account.language.toUpperCase(),
                    account.language.toUpperCase()
            });
        }
        // Replace Message placeholder
        format = format.replaceAll("%MESSAGE%", RankUtils.hasPermission(account, "chat.color") ? ChatHelper.replaceColor(msg) : msg);
        return format;
    }

    /**
     * Finds the value with the highest priority out of the list of ranks
     *
     * @param type  color, suffix or prefix
     * @param ranks list of the ranks to collect from
     */
    public static String getRankValue(String type, List<Rank> ranks) {
        int highestPriority = Integer.MIN_VALUE;
        String val = null;
        for (Rank rank : ranks)
            if (type.equalsIgnoreCase("suffix")) {
                if (rank.suffixPriority > highestPriority) {
                    highestPriority = rank.suffixPriority;
                    val = rank.suffix;
                }
            } else if (type.equalsIgnoreCase("prefix")) {
                if (rank.prefixPriority > highestPriority) {
                    highestPriority = rank.prefixPriority;
                    val = rank.prefix;
                }
            } else if (type.equalsIgnoreCase("color"))
                if (rank.colorPriority > highestPriority) {
                    highestPriority = rank.colorPriority;
                    val = rank.color;
                }
        return val;
    }

    public static String getName(EntityPlayer player, Account account) {
        if (account.displayName == null || account.displayName.isEmpty())
            return player.getDisplayNameString();
        else
            return account.displayName;
    }

    public static List<Rank> getRanks(Account account) {
        List<Rank> ranks = new ArrayList<>();
        for (String rank : account.rank) {
            Rank instance = SECore.dataLoader.get(DataLoader.DataType.RANK, rank, new Rank());
            if (instance != null)
                ranks.add(instance);
            else {
                // TODO Correct Invalid Rank
            }
        }
        return ranks;
    }
}
