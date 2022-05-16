package com.wurmcraft.serveressentials.common.modules.autorank;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.AutoRank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.autorank.event.RankupEvents;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Autorank", dependencies = "Rank")
public class ModuleAutorank {

  public void setup() {
    try {
      if (SECore.dataLoader.getFromKey(DataType.AUTORANK, new AutoRank()) == null
          || (SECore.dataLoader.getFromKey(DataLoader.DataType.AUTORANK, new AutoRank()).size()
              <= 0)) {
        setupDefaultRankups();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    MinecraftForge.EVENT_BUS.register(new RankupEvents());
  }

  public void reload() {
    for (String autoRank :
        SECore.dataLoader.getFromKey(DataLoader.DataType.AUTORANK, new AutoRank()).keySet()) {
      SECore.dataLoader.delete(DataLoader.DataType.AUTORANK, autoRank, true);
    }
  }

  private static void setupDefaultRankups() {
    AutoRank defaultToMember = new AutoRank("default", "member", 300, "", 0, "{}");
    if (SECore.dataLoader.get(DataType.AUTORANK, defaultToMember.rank) == null) {
      SECore.dataLoader.register(
          DataLoader.DataType.AUTORANK, defaultToMember.rank, defaultToMember);
    }
  }
}
