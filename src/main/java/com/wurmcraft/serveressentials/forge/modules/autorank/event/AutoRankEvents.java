package com.wurmcraft.serveressentials.forge.modules.autorank.event;

import com.wurmcraft.serveressentials.forge.modules.autorank.AutoRankModule;
import com.wurmcraft.serveressentials.forge.modules.autorank.utils.AutoRankUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class AutoRankEvents {

  @SubscribeEvent
  public void onWorldTick(WorldTickEvent e) {
    if (AutoRankModule.config != null && AutoRankModule.config.checkTimer > 0) {
      if (e.side.isServer()
          && e.world.getWorldTime() % AutoRankModule.config.checkTimer == 0) {
        for (EntityPlayer player : FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getPlayerList().getPlayers()) {
          AutoRankUtils.checkAndHandleRankup(player);
        }
      }
    }
  }
}
