package com.wurmcraft.serveressentials.forge.modules.protect.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.RegionHelper;
import java.util.NoSuchElementException;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ProtectLoadEvents {

  @SubscribeEvent
  public void onChunkLoad(ChunkEvent.Load e) {
    String regionID = RegionHelper.getIDForRegion(e.getChunk());
    try {
      SECore.dataHandler.getData(DataKey.CLAIM, regionID);
    } catch (NoSuchElementException ignored) {

    }
  }

//  @SubscribeEvent
//  public void onChunkLoad(ChunkEvent.Unload e) {
//    String regionID = RegionHelper.getIDForRegion(e.getChunk());
//    try {
//      SECore.dataHandler.delData(DataKey.CLAIM, regionID, false);
//    } catch (NoSuchElementException ignored) {
//
//    }
//  }
}
