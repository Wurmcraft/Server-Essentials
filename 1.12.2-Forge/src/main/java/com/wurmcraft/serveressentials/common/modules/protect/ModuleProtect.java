package com.wurmcraft.serveressentials.common.modules.protect;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.modules.protect.event.ClaimNotifyEvents;
import com.wurmcraft.serveressentials.common.modules.protect.event.ProtectionEvents;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "Protect")
public class ModuleProtect {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new ProtectionEvents());
    if (((ConfigProtect) SECore.moduleConfigs.get("PROTECT")).claimNotify) {
      MinecraftForge.EVENT_BUS.register(new ClaimNotifyEvents());
    }
  }

  public void reload() {
  }
}
