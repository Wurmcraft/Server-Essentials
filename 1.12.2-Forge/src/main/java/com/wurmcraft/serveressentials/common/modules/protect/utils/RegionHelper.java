package com.wurmcraft.serveressentials.common.modules.protect.utils;

import com.wurmcraft.serveressentials.common.modules.protect.models.RegionPos;
import net.minecraft.util.math.BlockPos;

public class RegionHelper {

  public static RegionPos getRegionPos(BlockPos pos, int dim) {
    return new RegionPos(((pos.getX() >> 4) / 32), ((pos.getY() >> 4) / 32),
        ((pos.getZ() >> 4) / 32), dim);
  }

  public static String convertToID(RegionPos pos) {
      return null;
  }
}
