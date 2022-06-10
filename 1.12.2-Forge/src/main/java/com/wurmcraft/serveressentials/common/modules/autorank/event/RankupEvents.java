package com.wurmcraft.serveressentials.common.modules.autorank.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.AutoRank;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.command.EcoUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.autorank.ConfigAutorank;
import com.wurmcraft.serveressentials.common.modules.autorank.ModuleAutorank;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class RankupEvents {

  private final HashMap<String, Long> lastCheck = new HashMap<>();

  @SubscribeEvent
  public void onWorldTick(TickEvent.PlayerTickEvent e) {
    if (e.side.equals(Side.SERVER)) {
      if (lastCheck.containsKey(e.player.getGameProfile().getId().toString())) {
        if (System.currentTimeMillis()
            >= lastCheck.get(e.player.getGameProfile().getId().toString())) {
          Account account =
              SECore.dataLoader.get(
                  DataType.ACCOUNT, e.player.getGameProfile().getId().toString(),
                  new Account());
          checkAndHandleUpdate(e.player, account);
          lastCheck.put(account.uuid, System.currentTimeMillis() + 5000);
        }
      } else {
        lastCheck.put(e.player.getGameProfile().getId().toString(),
            System.currentTimeMillis());
      }
    }
  }

  public static void checkAndHandleUpdate(EntityPlayer player, Account account) {
    if (SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank()) != null) {
      for (String ar : SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank())
          .keySet()) {
        AutoRank autoRank = SECore.dataLoader.getFromKey(DataType.AUTORANK,
                new AutoRank())
            .get(ar);
        List<Rank> userRanks = PlayerUtils.getUserRanks(account);
        for (Rank rank : userRanks) {
          if (canRankup(account, rank, autoRank)) {
            rankup(player, autoRank);
          }
        }
      }
    }
  }

  public static boolean canRankup(Account account, Rank currentRank, AutoRank rank) {
    if (currentRank == null || rank == null || !rank.rank.equals(currentRank.name)) {
      return false;
    }
    // Playtime check
    long totalPlaytime = PlayerUtils.getTotalPlaytime(account);
    if (totalPlaytime < rank.playtime) {
      return false;
    }
    // Currency Check
    if (SECore.modules.get("ECONOMY") != null
        && !EcoUtils.canBuy(rank.currency_name, rank.currency_amount, account)) {
      return false;
    }
    // Check special cases
    // TODO Add Special Functions / cases
    return true;
  }

  public static void rankup(EntityPlayer player, AutoRank autoRank) {
    Account account = PlayerUtils.getLatestAccount(
        player.getGameProfile().getId().toString());
    if (account != null) {
      List<String> userRanks = new ArrayList<>();
      Collections.addAll(userRanks, account.rank);
      userRanks.remove(autoRank.rank);
      userRanks.add(autoRank.next_rank);
      account.rank = userRanks.toArray(new String[0]);
      SECore.dataLoader.update(
          DataType.ACCOUNT, player.getGameProfile().getId().toString(), account);
      notifyRankup(player, autoRank);
    } else {
      ServerEssentials.LOG.warn(
          "Failed to rankup user '"
              + player.getGameProfile().getId().toString()
              + "' Unable to pull updated data from api");
    }
  }

  private static void notifyRankup(EntityPlayer player, AutoRank autoRank) {
    Account account = SECore.dataLoader.get(DataType.ACCOUNT,
        player.getGameProfile().getId().toString(), new Account());
    Language userLang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE, account.lang,
        new Language());
    ChatHelper.send(player, userLang.ANNOUNCEMENT_RANKUP_PERSONAL.replaceAll("\\{@RANK@}",
        autoRank.next_rank));
    if (((ConfigAutorank) SECore.moduleConfigs.get("AUTORANK")).announceRackup) {
      for (EntityPlayer otherPlayer : FMLCommonHandler.instance()
          .getMinecraftServerInstance().getPlayerList()
          .getPlayers()) {
        Language lang = SECore.dataLoader.get(DataLoader.DataType.LANGUAGE,
            SECore.dataLoader.get(DataLoader.DataType.ACCOUNT,
                player.getGameProfile().getId().toString(), new Account()).lang,
            new Language());
        ChatHelper.send(otherPlayer,
            lang.ANNOUNCEMENT_RANKUP_SERVER.replaceAll("\\{@RANK@}", autoRank.next_rank)
                .replaceAll("\\{@NAME@}", ChatHelper.getName(player, account)));
      }
    }
    // TODO Rankup notification on bridge (depending on configuration)
  }
}
