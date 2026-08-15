package com.wurmcraft.serveressentials.common.modules.transfer;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.loader.RestDataLoader;
import com.wurmcraft.serveressentials.common.modules.statistics.ConfigStatistics;
import joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.List;

@Module(
    name = "Transfer",
    dependencies = {"Core"})
public class ModuleTransfer {

  public void setup() {
    if (SECore.dataLoader instanceof RestDataLoader) {

    } else {
      ServerEssentials.LOG.warn("Module 'Transfer' does not work in 'File' Storage mode!");
      SECore.modules.remove("TRANSFER");
    }
  }

  public void reload() {}

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigTransfer cfg = (ConfigTransfer) SECore.moduleConfigs.get("TRANSFER");
    info.add("transferID: " + cfg.transferID);
    info.add("allowLargeTransfers: " + cfg.allowLargeTransfers);
    info.add("costToTransferItem: " + cfg.costToTransferItem);
    info.add("itemBlacklist: " + Strings.join(cfg.itemBlacklist, ", "));
    return info.toArray(new String[0]);
  }
}
