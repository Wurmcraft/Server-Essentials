package com.wurmcraft.serveressentials.forge.modules.rank.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import net.minecraft.command.ICommandSender;

public class RankUtils {

  public static void createDefaultRanks() {
    Rank defaultRank = new Rank("Default","&7[Default]", "&7",new String[0],new String[] {"command.help", "general.tpa", "general.tpaccept", "general.home", "general.sethome","general.delhome","general.spawn", "economy.pay", "economy.balance", "language.lang", "command.rtp", "autorank.ar"});
    Rank memberRank = new Rank("Member", "&7[&6Member&7]", "&7", new String[] {"Default"}, new String[] {""});
    Rank adminRank = new Rank("Admin", "&c[&4Admin&c]","&6",new String[] {"Member"}, new String[] {"*"});
    SECore.dataHandler.registerData(DataKey.RANK, defaultRank);
    SECore.dataHandler.registerData(DataKey.RANK, memberRank);
    SECore.dataHandler.registerData(DataKey.RANK, adminRank);
  }

  public static boolean hasPermission(ICommandSender sender, String node) {
    return true;
  }
}
