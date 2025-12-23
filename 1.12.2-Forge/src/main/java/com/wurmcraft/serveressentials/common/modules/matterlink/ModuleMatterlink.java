package com.wurmcraft.serveressentials.common.modules.matterlink;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.modules.matterlink.event.BridgeEvents;
import com.wurmcraft.serveressentials.common.modules.matterlink.utils.MatterBridgeUtils;
import net.minecraftforge.common.MinecraftForge;

@Module(
    name = "Matterlink",
    dependencies = {"Chat"})
public class ModuleMatterlink {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new BridgeEvents());
    ServerEssentials.LOG.info("Bridge Status: {}", MatterBridgeUtils.getHealth());
  }

  public void reload() {}
}
