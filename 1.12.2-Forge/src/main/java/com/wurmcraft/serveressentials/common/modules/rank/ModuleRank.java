package com.wurmcraft.serveressentials.common.modules.rank;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.Rank;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.protect.ConfigProtect;
import com.wurmcraft.serveressentials.common.modules.protect.event.ClaimNotifyEvents;

import java.util.ArrayList;
import java.util.List;

@Module(
    name = "Rank",
    dependencies = {"Core"})
public class ModuleRank {

  public void setup() {
    try {
      if (SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()) == null
          || (SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()).size() <= 0)) {
        setupDefaultRanks();
      }
    } catch (Exception e) {
      ServerEssentials.LOG.warn("Failed to load rank module! ({})", e.getMessage());
    }
  }

  public void reload() {
    for (String rank :
        SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()).keySet()) {
      SECore.dataLoader.delete(DataLoader.DataType.RANK, rank, true);
    }
  }

  public void setupDefaultRanks() {
    Rank defaultRank =
        new Rank(
            ((ConfigRank) SECore.moduleConfigs.get("RANK")).defaultRank.toLowerCase(),
            new String[] {
              "command.help",
              "command.home",
              "command.sethome",
              "command.tpa",
              "command.tpaccept",
              "command.tpadeny",
              "command.spawn",
              "command.autorank"
            },
            new String[] {},
            "&8[&7Default&8]",
            0,
            "&7",
            0,
            "&3",
            0);
    Rank member =
        new Rank(
            "member",
            new String[] {"command.warp", "command.enderchest"},
            new String[] {((ConfigRank) SECore.moduleConfigs.get("RANK")).defaultRank},
            "&8[&eMember&8]",
            1,
            "&7",
            1,
            "&b",
            1);
    Rank admin =
        new Rank(
            "admin",
            new String[] {"*"},
            new String[] {"Member"},
            "&c[&4Admin&c]",
            2,
            "&7",
            2,
            "&b",
            2);
    if (SECore.dataLoader.get(DataLoader.DataType.RANK, defaultRank.name) == null) {
      SECore.dataLoader.register(DataLoader.DataType.RANK, defaultRank.name, defaultRank);
    }
    if (SECore.dataLoader.get(DataLoader.DataType.RANK, member.name) == null) {
      SECore.dataLoader.register(DataLoader.DataType.RANK, member.name, member);
    }
    if (SECore.dataLoader.get(DataLoader.DataType.RANK, admin.name) == null) {
      SECore.dataLoader.register(DataLoader.DataType.RANK, admin.name, admin);
    }
  }

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigRank cfg = (ConfigRank) SECore.moduleConfigs.get("RANK");
    info.add("defaultRank: " + cfg.defaultRank);
    List<String> ranks = new ArrayList<>();
    for(Rank rank : SECore.dataLoader.getFromKey(DataLoader.DataType.RANK, new Rank()).values())
      ranks.add(rank.name);
    info.add("Ranks: (" + ranks.size() + ") " + String.join(", ", ranks));
    return info.toArray(new String[0]);
  }
}
