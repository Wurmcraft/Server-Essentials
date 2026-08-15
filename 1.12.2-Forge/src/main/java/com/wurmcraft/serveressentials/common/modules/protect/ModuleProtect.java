package com.wurmcraft.serveressentials.common.modules.protect;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.modules.matterlink.ConfigMatterlink;
import com.wurmcraft.serveressentials.common.modules.protect.event.ClaimNotifyEvents;
import com.wurmcraft.serveressentials.common.modules.protect.event.ProtectionEvents;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

@Module(name = "Protect")
public class ModuleProtect {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new ProtectionEvents());
    if (((ConfigProtect) SECore.moduleConfigs.get("PROTECT")).claimNotify) {
      MinecraftForge.EVENT_BUS.register(new ClaimNotifyEvents());
    }
  }

  public void reload() {}

  public String[] generateModuleInfo(Language lang) {
    List<String> info = new ArrayList<>();
    ConfigProtect cfg = (ConfigProtect) SECore.moduleConfigs.get("PROTECT");
    info.add("defaultType: " + cfg.defaultType);
    info.add("defenseRange: " + cfg.defenseRange);
    info.add("minClaimSize: " + cfg.minClaimSize);
    info.add("preventNearbyExplosions: " + cfg.preventNearbyExplosions);
    info.add("claimNotify: " + cfg.claimNotify);
    info.add("trackingUpdateTimeTicks: " + cfg.trackingUpdateTimeTicks);
    info.add("counter: " + ClaimNotifyEvents.counter);
    info.add("loaded locations: " + ClaimNotifyEvents.locationCache.size());
    info.add("Claims: " + ClaimNotifyEvents.lastClaim.size());
    return info.toArray(new String[0]);
  }
}
