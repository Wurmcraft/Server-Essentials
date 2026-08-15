package com.wurmcraft.serveressentials.common.modules.matterlink;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.modules.logging.ConfigLogging;
import com.wurmcraft.serveressentials.common.modules.matterlink.event.BridgeEvents;
import com.wurmcraft.serveressentials.common.modules.matterlink.utils.MatterBridgeUtils;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

@Module(
    name = "Matterlink",
    dependencies = {"Chat"})
public class ModuleMatterlink {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new BridgeEvents());
    ServerEssentials.LOG.info("Bridge Status: {}", MatterBridgeUtils.getHealth());
  }

  public void reload() {}

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigMatterlink cfg = (ConfigMatterlink) SECore.moduleConfigs.get("MATTERLINK");
    info.add("url: " +  cfg.url);
    info.add("gateway: " +  cfg.gateway);
    info.add("protocol: " +  cfg.protocol);
    info.add("account: " +  cfg.account);
    info.add("token: " +  (cfg.token.isEmpty() ? "empty" : "*****"));
    info.add("displayLoginLogoutMessages: " +  cfg.displayLoginLogoutMessages);
    info.add("displayServerStatus: " +  cfg.displayServerStatus);
    info.add("dataCollectionType: " +  cfg.dataCollectionType);
    return info.toArray(new String[0]);
  }
}
