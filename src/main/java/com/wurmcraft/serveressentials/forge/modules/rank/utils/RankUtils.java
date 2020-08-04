package com.wurmcraft.serveressentials.forge.modules.rank.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.command.SECommand;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.*;
import java.util.Collections;
import java.util.NoSuchElementException;
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
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      return hasPermission((EntityPlayer) sender.getCommandSenderEntity(),
          RankUtils.getRank(sender), node);
    }
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
        createDefaultRanks();
      }
    } else {
      Rank[] ranks = SECore.dataHandler.getDataFromKey(DataKey.RANK, new Rank()).values()
          .toArray(new Rank[0]);
      if (ranks.length <= 0) {
        createDefaultRanks();
      }
    }
  }

  public static Rank getRank(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      StoredPlayer playerData = PlayerUtils
          .get((EntityPlayer) sender.getCommandSenderEntity());
      try {
        return (Rank) SECore.dataHandler.getData(DataKey.RANK, playerData.global.rank);
      } catch (NoSuchElementException e) {
        return new Rank();
      }
    }
    return null;
  }

  public static boolean hasPermission(EntityPlayer player, Rank rank, String perm) {
    String[] rankPermissions = getPermissions(player, rank);
    String[] permShredder = perm.split("\\.");
    String module = permShredder[0];
    String command = permShredder[1];
    String subCommand = "*";
    if (permShredder.length == 3) {
      subCommand = permShredder[2];
    }
    for (String p : rankPermissions) {
      if (p.isEmpty()) {
        continue;
      }
      if (p.equals("*")) {
        return true;
      } else {
        String[] splitNode = p.split("\\.");
        String cm = splitNode[0];
        String ccm = splitNode[1];
        String csc = "*";
        boolean quickCheck = module.equalsIgnoreCase(cm) && command.equalsIgnoreCase(ccm);
        if (splitNode.length == 3) {
          csc = splitNode[2];
        }
        if (!csc.equals("*") && !subCommand.equals("*")) {
          if (quickCheck && subCommand.equalsIgnoreCase(csc)) {
            return true;
          }
        } else if (quickCheck) {
          return true;
        }
      }
    }
    return false;
  }

  public static String[] getPermissions(EntityPlayer player, Rank rank) {
    List<String> permissionNodes = new ArrayList<>();
    Collections.addAll(permissionNodes, rank.permission);
    if (rank.inheritance != null && rank.inheritance.length > 0) {
      for (String ih : rank.inheritance) {
        try {
          Rank inh = (Rank) SECore.dataHandler.getData(DataKey.RANK, ih);
          Collections.addAll(permissionNodes, getPermissions(player, inh));
        } catch (NoSuchElementException e) {
          ServerEssentialsServer.LOGGER.warn("Unable to find rank '" + ih + "'");
        }
      }
    }
    StoredPlayer playerData = PlayerUtils.get(player);
    permissionNodes.addAll(Arrays.asList(playerData.global.extraPerms));
    return permissionNodes.toArray(new String[0]);
  }
}
