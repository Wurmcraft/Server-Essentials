package com.wurmcraft.serveressentials.forge.modules.autorank.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.modules.autorank.AutoRankModule;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.NoSuchElementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class AutoRankUtils {

  private static void createDefaultAutoRanks() {
    AutoRank defaultToMember = new AutoRank("Default", "Member", 600);
    SECore.dataHandler.registerData(DataKey.AUTO_RANK, defaultToMember);
  }

  public static void checkAndLoadAutoRanks() {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      AutoRank[] ranks = RestRequestHandler.AutoRank.getAutoRanks();
      AutoRank[] fileRanks = SECore.dataHandler
          .getDataFromKey(DataKey.AUTO_RANK, new AutoRank())
          .values().toArray(new AutoRank[0]);
      if (ranks != null && ranks.length > 0) {
        ServerEssentialsServer.isUpdateInProgress = true;
        for (AutoRank fileRank : fileRanks) {
          SECore.dataHandler.delData(DataKey.AUTO_RANK, fileRank.getID(), true);
        }
        ServerEssentialsServer.isUpdateInProgress = false;
        for (AutoRank r : ranks) {
          SECore.dataHandler.registerData(DataKey.AUTO_RANK, r);
        }
      } else {
        createDefaultAutoRanks();
      }
    } else {
      AutoRank[] ranks = SECore.dataHandler
          .getDataFromKey(DataKey.AUTO_RANK, new AutoRank()).values()
          .toArray(new AutoRank[0]);
      if (ranks.length <= 0) {
        createDefaultAutoRanks();
      }
    }
  }

  public static void updateAutoRank(AutoRank rank) {
    SECore.dataHandler.registerData(DataKey.AUTO_RANK, rank);
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      RestRequestHandler.AutoRank.overrideAutoRank(rank);
    }
  }

  public static AutoRank getNextUpdatePath(EntityPlayer player) {
    AutoRank[] rankup = SECore.dataHandler
        .getDataFromKey(DataKey.AUTO_RANK, new AutoRank()).values()
        .toArray(new AutoRank[0]);
    if (rankup.length > 0) {
      String rank = PlayerUtils.get(player).global.rank;
      for (AutoRank ar : rankup) {
        if (ar.rank.equals(rank)) {
          return ar;
        }
      }
    }
    return null;
  }

  public static boolean isReadyToRankup(EntityPlayer player, AutoRank rank) {
    return rank != null && PlayerUtils.get(player).global.rank.equals(rank.rank)
        && rank.exp <= player.experienceLevel && rank.playTime <= PlayerUtils
        .getTotalPlayTime(player);
  }

  public static void checkAndHandleRankup(EntityPlayer player) {
    if (isReadyToRankup(player, getNextUpdatePath(player))) {
      AutoRank autoRank = getNextUpdatePath(player);
      try {
        Rank nextRank = (Rank) SECore.dataHandler
            .getData(DataKey.RANK, autoRank.nextRank);
        RankUtils.changeRank(player, nextRank);
        ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).RANK_CHANGE.replaceAll("%RANK%", nextRank.name));
        if (AutoRankModule.config.announceAutoRank) {
          for (EntityPlayer p : FMLCommonHandler.instance().getMinecraftServerInstance()
              .getPlayerList().getPlayers()) {
            if (!p.getGameProfile().getId().equals(player.getGameProfile().getId())) {
              ChatHelper.sendMessage(p, PlayerUtils.getLanguage(p).ANNOUNCEMENT_AUTORANK.replaceAll("%PLAYER%", player.getDisplayNameString()).replaceAll("%RANK%", nextRank.name));
            }
          }
        }
      } catch (NoSuchElementException e) {
        ServerEssentialsServer.LOGGER.warn(
            "Tried to Rankup '" + player.getDisplayNameString() + "' however rank '"
                + autoRank.nextRank + "' does not exist!");
      }
    }
  }

}
