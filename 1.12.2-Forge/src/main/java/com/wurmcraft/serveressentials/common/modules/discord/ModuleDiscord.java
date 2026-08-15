package com.wurmcraft.serveressentials.common.modules.discord;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.config.ConfigGlobal;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.modules.core.event.PlayerDataTrackerEvent;
import joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.List;

@Module(name = "Discord")
public class ModuleDiscord {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {

    } else {
      ServerEssentials.LOG.warn("Module 'Discord' does not work in 'File' Storage mode!");
      SECore.modules.remove("DISCORD");
    }
  }

  public void reload() {}

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigDiscord cfg = (ConfigDiscord) SECore.moduleConfigs.get("DISCORD");
    info.add("verifyCommands: " + Strings.join(cfg.verifyCommands, ", "));
    return info.toArray(new String[0]);
  }
}
