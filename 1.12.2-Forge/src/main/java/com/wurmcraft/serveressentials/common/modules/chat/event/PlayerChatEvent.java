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
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.chat.ConfigChat;
import com.wurmcraft.serveressentials.common.modules.economy.ConfigEconomy;
import com.wurmcraft.serveressentials.common.modules.rank.ConfigRank;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.UsernameCache;
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
    if (((ConfigChat) SECore.moduleConfigs.get("CHAT")).displayUUIDOnHover) {
      UUID uuid = e.getPlayer().getGameProfile().getId();
      TextComponentString hoverText =
          new TextComponentString(
              TextFormatting.GOLD
                  + UsernameCache.getLastKnownUsername(uuid)
                  + TextFormatting.AQUA
                  + " ("
                  + uuid.toString()
                  + ")");
      message.setStyle(
          message.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)));
    }
    e.setComponent(message);
    ChatHelper.sendFrom(e.getPlayer(), ch, message);
    if (ch.logChat) {
      logChat(ch, message.getText());
    }
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
    if (account == null || account.mute_time == null) {
      LOG.warn("Player tried to talk but was unable to, Unable to correct!");
      return false;
    }
    if (account.mute_time < Instant.EPOCH.getEpochSecond()) {
      Account latestAccount = PlayerUtils.getLatestAccount(account.uuid);
      if (latestAccount.muted && account.mute_time < Instant.EPOCH.getEpochSecond()) {
        latestAccount.muted = false;
        SECore.dataLoader.update(DataType.ACCOUNT, account.uuid, latestAccount);
        return latestAccount.muted;
      }
    }
    return account.muted;
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
    if (channel.chatReplacement != null && channel.chatReplacement.size() > 0) {
      for (String regex : channel.chatReplacement.keySet()) {
        msg = msg.replaceAll(regex, ChatHelper.replaceColor(channel.chatReplacement.get(regex)));
      }
    }
    // Player Replacement
    if (RankUtils.hasPermission(account, "chat.replacement")) {
      msg =
          StringUtils.replaceEach(
              msg,
              PLAYER_REPLACEMENT,
              new String[] {
                String.format(
                    "%.2f",
                    EcoUtils.balance(
                        account,
                        ((ConfigEconomy) SECore.moduleConfigs.get("ECONOMY")).serverCurrency)),
                String.format("%.4f", player.experience),
                player.experienceLevel + "",
                (PlayerUtils.getTotalPlaytime(
                            SECore.dataLoader.get(
                                DataType.ACCOUNT,
                                player.getGameProfile().getId().toString(),
                                new Account()))
                        / 1440)
                    + "d", // min -> days
                (PlayerUtils.getTotalPlaytime(
                            SECore.dataLoader.get(
                                DataType.ACCOUNT,
                                player.getGameProfile().getId().toString(),
                                new Account()))
                        / 1440)
                    + "d", // min -> days
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
    for (Rank rank : ranks) {
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
      } else if (type.equalsIgnoreCase("color")) {
        if (rank.color_priority > highestPriority) {
          highestPriority = rank.color_priority;
          val = rank.color;
        }
      }
    }
    return val;
  }

  public static List<Rank> getRanks(Account account) {
    List<Rank> ranks = new ArrayList<>();
    boolean foundInvalid = false;
    for (String rank : account.rank) {
      Rank instance = SECore.dataLoader.get(DataLoader.DataType.RANK, rank, new Rank());
      if (instance != null) {
        ranks.add(instance);
      } else {
        foundInvalid = true;
      }
    }
    correctInvalidRanks(account);
    // Add errored rank is none are found
    if (ranks.size() == 0) {
      Rank erroredRank =
          new Rank("Error", new String[] {}, new String[] {}, "[Error]", 0, "", 0, "", 0);
      ranks.add(erroredRank);
    }
    return ranks;
  }

  public static void correctInvalidRanks(Account account) {
    List<Rank> validUserRanks = new ArrayList<>();
    if (account != null) {
      for (String rank : account.rank) {
        Rank r = RankUtils.getRank(rank);
        if (r != null) {
          validUserRanks.add(r);
        }
      }
      String[] ranks = new String[validUserRanks.size()];
      for (int index = 0; index < validUserRanks.size(); index++) {
        ranks[index] = validUserRanks.get(index).name.toLowerCase();
      }
      if (validUserRanks.size() < account.rank.length) {
        LOG.warn("User '" + account.uuid + "' has an invalid rank!, This is being corrected");
      }
      if (ranks.length == 0) {
        ranks = new String[] {((ConfigRank) SECore.moduleConfigs.get("RANK")).defaultRank};
        LOG.warn("User has no valid ranks, adding default rank!");
      }
      account.rank = ranks;
    }
  }
}
