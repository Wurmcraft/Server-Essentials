package com.wurmcraft.serveressentials.common.modules.chatbridge;

import com.wurmcraft.serveressentials.api.loading.Module;
import com.wurmcraft.serveressentials.common.modules.chatbridge.event.BridgeEvents;
import net.minecraftforge.common.MinecraftForge;

@Module(name = "ChatBridge", dependencies = {"Chat"})
public class ModuleChatBridge {

  public void setup() {
    MinecraftForge.EVENT_BUS.register(new BridgeEvents());
  }

  public void reload() {

  }
}
