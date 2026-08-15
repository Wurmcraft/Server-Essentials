package com.wurmcraft.serveressentials.common.modules.autorank;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.AutoRank;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.autorank.event.RankupEvents;
import joptsimple.internal.Strings;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Module(name = "Autorank", dependencies = "Rank")
public class ModuleAutorank {

  public void setup() {
    try {
      if (SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank()) == null
          || (SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank()).isEmpty())) {
        setupDefaultRankups();
      }
    } catch (Exception e) {
      ServerEssentials.LOG.warn("Failed to create default ranks! ({})", e.getMessage());
    }
    validateAutoRanks();
    MinecraftForge.EVENT_BUS.register(new RankupEvents());
  }

  public void reload() {
    for (String autoRank :
        SECore.dataLoader.getFromKey(DataLoader.DataType.AUTORANK, new AutoRank()).keySet()) {
      SECore.dataLoader.delete(DataLoader.DataType.AUTORANK, autoRank, true);
    }
    validateAutoRanks();
  }

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigAutorank cfg = (ConfigAutorank) SECore.moduleConfigs.get("AUTORANK");
    info.add("announceRackup: " + cfg.announceRackup);
    AutoRank[] autoranks = SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank()).values().toArray(new AutoRank[0]);
    List<String> autoRanks = Arrays.stream(autoranks).map(ar -> ar.rank).collect(Collectors.toList());
    info.add("AutoRanks: " + autoranks.length+ " (" + Strings.join(autoRanks, ", ") + ")");
    return info.toArray(new String[0]);
  }

  public static void setupDefaultRankups() {
    AutoRank defaultToMember = new AutoRank("default", "member", 300, "", 0, "{}");
    if (SECore.dataLoader.get(DataType.AUTORANK, defaultToMember.rank) == null) {
      SECore.dataLoader.register(
          DataLoader.DataType.AUTORANK, defaultToMember.rank, defaultToMember);
    }
  }

  public static void validateAutoRanks() {
    for (AutoRank ar : SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank()).values()) {
      if (!isValidAutoRank(ar)) {
        SECore.dataLoader.delete(DataType.AUTORANK, ar.rank, true);
        ServerEssentials.LOG.warn("Invalid AutoRank detected! '{} -> {}'", ar.rank, ar.next_rank);
      }
    }
  }

  public static boolean isValidAutoRank(AutoRank ar) {
    return SECore.dataLoader.get(DataType.RANK, ar.next_rank, new Rank()) != null;
  }
}
