package com.wurmcraft.serveressentials.common.modules.chat.event;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;
import static com.wurmcraft.serveressentials.common.data.ConfigLoader.SAVE_DIR;
import static com.wurmcraft.serveressentials.common.utils.ChatHelper.getName;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Channel;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.command.EcoUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public class PlayerChatEvent {
  public static final String[] REPLACE_LIST =
      new String[] {
        "%USERNAME%",
        "%NAME%",
        "%DIMENSION%",
        "%RANK_PREFIX%",
        "%RANK_SUFFIX%",
        "%CHANNEL_PREFIX%",
        "%SERVER_ID%"
      };

  public static final String[] PLAYER_REPLACEMENT =
      new String[] {
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
    LocalAccount local =
        SECore.dataLoader.get(
            DataLoader.DataType.LOCAL_ACCOUNT,
            e.getPlayer().getGameProfile().getId().toString(),
            new LocalAccount());
    if (local.channel == null) {
      LOG.debug("Adding missing default channel to '" + local.uuid + "'");
      local.channel = ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChannel;
      SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, local.uuid, local);
    }
    Account account =
        SECore.dataLoader.get(
            DataLoader.DataType.ACCOUNT,
            e.getPlayer().getGameProfile().getId().toString(),
            new Account());
    if (isMuted(account)) {
      Language lang =
          SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, account.lang, new Language());
      ChatHelper.send(e.getPlayer(), lang.MUTED);
      e.setCanceled(true);
      return;
    }
    Channel ch = SECore.dataLoader.get(DataLoader.DataType.CHANNEL, local.channel, new Channel());
    if (!ch.enabled && !RankUtils.hasPermission(account, "chat.pause.bypass")) {
      Language lang =
          SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, account.lang, new Language());
      ChatHelper.send(e.getPlayer(), lang.CHANNEL_DISABLED);
      e.setCanceled(true);
      return;
    }
    TextComponentString message =
        new TextComponentString(format(e.getPlayer(), ch, account, e.getMessage()));
    e.setComponent(message);
    ChatHelper.sendFrom(e.getPlayer(), ch, message);
    if (ch.logChat) logChat(ch, message.getText());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onChatCancel(ServerChatEvent e) {
    e.setCanceled(true);
  }

  private void logChat(Channel ch, String message) {
    message = log_format.format(new Date()) + " " + message + "\n";
    File logFile =
        new File(
            SAVE_DIR
                + File.separator
                + "Logs"
                + File.separator
                + "channel"
                + File.separator
                + ch.name
                + ".log");
    try {
      if (!logFile.exists()) {
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
      }
      Files.write(
          logFile.toPath(),
          message.getBytes(),
          StandardOpenOption.WRITE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      e.printStackTrace();
      LOG.warn("Failed to write chat log for channel '" + ch.name + "'");
    }
  }

  private boolean isMuted(Account account) {
    return account.muted; // TODO Check for temp mute
  }

  public static String format(EntityPlayer player, Channel channel, Account account, String msg) {
    String format =
        channel.chatFormat.isEmpty()
            ? ChatHelper.replaceColor(
                ((ConfigChat) SECore.moduleConfigs.get("CHAT")).defaultChatFormat)
            : ChatHelper.replaceColor(channel.chatFormat);
    List<Rank> ranks = getRanks(account);
    format =
        StringUtils.replaceEach(
            format,
            REPLACE_LIST,
            new String[] {
              ChatHelper.replaceColor(getRankValue("color", ranks))
                  + player.getGameProfile().getName(),
              ChatHelper.replaceColor(getRankValue("color", ranks)) + getName(player, account),
              player.dimension + "",
              ChatHelper.replaceColor(getRankValue("prefix", ranks)),
              ChatHelper.replaceColor(getRankValue("suffix", ranks)),
              ChatHelper.replaceColor(channel.prefix),
              ServerEssentials.config.general.serverID
            });
    // Channel Specific
    if (channel.chatReplacment != null && channel.chatReplacment.size() > 0)
      for (String regex : channel.chatReplacment.keySet())
        msg = msg.replaceAll(regex, ChatHelper.replaceColor(channel.chatReplacment.get(regex)));
    // Player Replacement
    if (RankUtils.hasPermission(account, "chat.replacement")) {
      msg =
          StringUtils.replaceEach(
              msg,
              PLAYER_REPLACEMENT,
              new String[] {
                String.format(
                    "%.2f", EcoUtils.balance(account, "default")), // TODO Default currency config
                String.format("%.4f", player.experience),
                player.experienceLevel + "",
                "0d", // TODO Get Total Playtime
                "0d", // TODO Get Total Playtime
                account.reward_points + "",
                account.reward_points + "",
                account.lang.toUpperCase(),
                account.lang.toUpperCase()
              });
    }
    // Replace Message placeholder
    format =
        format.replaceAll(
            "%MESSAGE%",
            RankUtils.hasPermission(account, "chat.color") ? ChatHelper.replaceColor(msg) : msg);
    return format;
  }

  /**
   * Finds the value with the highest priority out of the list of ranks
   *
   * @param type color, suffix or prefix
   * @param ranks list of the ranks to collect from
   */
  public static String getRankValue(String type, List<Rank> ranks) {
    int highestPriority = Integer.MIN_VALUE;
    String val = null;
    for (Rank rank : ranks)
      if (type.equalsIgnoreCase("suffix")) {
        if (rank.suffix_priority > highestPriority) {
          highestPriority = rank.suffix_priority;
          val = rank.suffix;
        }
      } else if (type.equalsIgnoreCase("prefix")) {
        if (rank.prefix_priority > highestPriority) {
          highestPriority = rank.prefix_priority;
          val = rank.prefix;
        }
      } else if (type.equalsIgnoreCase("color"))
        if (rank.color_priority > highestPriority) {
          highestPriority = rank.color_priority;
          val = rank.color;
        }
    return val;
  }

  public static List<Rank> getRanks(Account account) {
    List<Rank> ranks = new ArrayList<>();
    for (String rank : account.rank) {
      Rank instance = SECore.dataLoader.get(DataLoader.DataType.RANK, rank, new Rank());
      if (instance != null) ranks.add(instance);
      else {
        // TODO Correct Invalid Rank
      }
    }
    // Add errored rank is none are found
    if (ranks.size() == 0) {
      Rank erroredRank =
          new Rank("Error", new String[] {}, new String[] {}, "[Error]", 0, "", 0, "", 0);
      ranks.add(erroredRank);
    }

    return ranks;
  }
}
