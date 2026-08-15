package com.wurmcraft.serveressentials.common.modules.statistics;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;

import java.util.ArrayList;
import java.util.List;

@Module(name = "Statistics", dependencies = "Core")
public class ModuleStatistics {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {

    } else {
      ServerEssentials.LOG.warn("Module 'Statistics' does not work in 'File' Storage mode!");
      SECore.modules.remove("STATISTICS");
    }
  }

  public void reload() {}

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigStatistics cfg = (ConfigStatistics) SECore.moduleConfigs.get("STATISTICS");
    info.add("player>deaths: " + cfg.player.deaths);
    info.add("server>loadedChunks: " + cfg.server.loadedChunks);
    info.add("server>ms: " + cfg.server.ms);
    info.add("server>playerCount: " + cfg.server.playerCount);
    info.add("world>blockBroken: " + cfg.world.blockBroken);
    info.add("world>blocksPlaced: " + cfg.world.blocksPlaced);
    info.add("world>entityKills: " + cfg.world.entityKills);
    info.add("world>groundItems: " + cfg.world.groundItems);
    return info.toArray(new String[0]);
  }
}
