package com.wurmcraft.serveressentials.forge.modules.rank.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class RankUtils {

  public static void createDefaultRanks() {
    Rank defaultRank = new Rank("Default", "&7[Default]", "&7", new String[0],
        new String[]{"command.help", "general.tpa", "general.tpaccept", "general.home",
            "general.sethome", "general.delhome", "general.spawn", "economy.pay",
            "economy.balance", "language.lang", "command.rtp", "autorank.ar"});
    Rank memberRank = new Rank("Member", "&7[&6Member&7]", "&7", new String[]{"Default"},
        new String[]{""});
    Rank adminRank = new Rank("Admin", "&c[&4Admin&c]", "&6", new String[]{"Member"},
        new String[]{"*"});
    SECore.dataHandler.registerData(DataKey.RANK, defaultRank);
    SECore.dataHandler.registerData(DataKey.RANK, memberRank);
    SECore.dataHandler.registerData(DataKey.RANK, adminRank);
  }

  public static boolean hasPermission(ICommandSender sender, String node) {
    return true;
  }

  public static void changeRank(EntityPlayer player, Rank rank) {
    StoredPlayer playerData = PlayerUtils.get(player);
    playerData.global.rank = rank.getID();
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      GlobalPlayer globalData = RestRequestHandler.User
          .getPlayer(player.getGameProfile().getId().toString());
      globalData.rank = rank.getID();
      RestRequestHandler.User
          .overridePlayer(player.getGameProfile().getId().toString(), globalData);
    }
  }

  public static void updateRank(Rank rank) {
    SECore.dataHandler.registerData(DataKey.RANK, rank);
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      RestRequestHandler.Rank.overrideRank(rank);
    }
  }

  public static void checkAndLoadRanks() {
    if (SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      Rank[] ranks = RestRequestHandler.Rank.getAllRanks();
      Rank[] fileRanks = SECore.dataHandler.getDataFromKey(DataKey.RANK, new Rank())
          .values().toArray(new Rank[0]);
      if (ranks != null && ranks.length > 0) {
        ServerEssentialsServer.isUpdateInProgress = true;
        for (Rank fileRank : fileRanks) {
          SECore.dataHandler.delData(DataKey.RANK, fileRank.getID(), true);
        }
        ServerEssentialsServer.isUpdateInProgress = false;
        for (Rank r : ranks) {
          SECore.dataHandler.registerData(DataKey.RANK, r);
        }
      } else {
        createDefaultRanks();;
      }
    } else {
      Rank[] ranks = SECore.dataHandler.getDataFromKey(DataKey.RANK, new Rank()).values().toArray(new Rank[0]);
      if(ranks.length <= 0) {
        createDefaultRanks();
      }
    }
  }
}
