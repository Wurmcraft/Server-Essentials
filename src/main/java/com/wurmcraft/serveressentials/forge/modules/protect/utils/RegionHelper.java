package com.wurmcraft.serveressentials.forge.modules.protect.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class RegionHelper {

  public static NonBlockingHashMap<ChunkPos, String> cacheChunk = new NonBlockingHashMap<>();
  public static NonBlockingHashMap<BlockPos, String> cacheBlockPos = new NonBlockingHashMap<>();

  public static String getIDForRegion(Chunk chunk) {
    if (cacheChunk.containsKey(chunk.getPos())) {
      return cacheChunk.get(chunk.getPos());
    }
    ChunkPos pos = chunk.getPos();
    String regionID =
        (pos.x / 32) + "-0-" + (pos.z / 31) + "_" + chunk.getWorld().provider
            .getDimension();
    cacheChunk.put(pos, regionID);
    return regionID;

  }

  public static String getIDForRegion(BlockPos pos, int dim) {
    if (cacheBlockPos.containsKey(pos)) {
      return cacheBlockPos.get(pos) + "_" + dim;
    }
    String regionID = ((pos.getX() >> 4) / 32) + "-" + ((pos.getY() >> 4) / 32) + "-" + (
        (pos.getZ() >> 4) / 32);
    cacheBlockPos.put(pos, regionID);
    return regionID + "_" + dim;
  }

}
