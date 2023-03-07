package com.wurmcraft.serveressentials.common.modules.protect.utils;

import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionClaim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionPos;
import net.minecraft.util.math.BlockPos;

public class RegionHelper {

  public static RegionPos getRegionPos(BlockPos pos, int dim) {
    return new RegionPos(((pos.getX() >> 4) / 32), ((pos.getY() >> 4) / 32),
        ((pos.getZ() >> 4) / 32), dim);
  }

  public static String convert(RegionPos pos) {
    return pos.dim + "_" + pos.x + "-" + pos.y + "-" + pos.z;
  }

  public static RegionPos convert(String regionID) {
    if (regionID != null && regionID.length() >= 7) {
      try {
        int dim = Integer.parseInt(regionID.substring(0, regionID.indexOf("_")));
        int x = Integer.parseInt(
            regionID.substring(regionID.indexOf("_"), regionID.indexOf("-")));
        int lastPos = regionID.indexOf("-");
        regionID = regionID.substring(lastPos);
        lastPos = regionID.indexOf("-");
        int y = Integer.parseInt(regionID.substring(0, lastPos));
        regionID = regionID.substring(lastPos);
        lastPos = regionID.indexOf("-");
        int z = Integer.parseInt(regionID.substring(0, lastPos));
        return new RegionPos(x, y, z, dim);
      } catch (Exception e) {
      }
    }
    return null;
  }

  public static Claim findClaim(BlockPos pos, RegionClaim claim) {
    if (claim != null) {
      for (Claim c : claim.claims) {
        if (matches(c, pos)) {
          return c;
        }
      }
    }
    return null;
  }

  public static boolean matches(Claim claim, BlockPos pos) {
    return claim.min.x <= pos.getX() && claim.max.x >= pos.getX()
        && claim.min.z <= pos.getZ() && claim.max.z >= pos.getZ()
        && claim.min.y <= pos.getY() && claim.max.y >= pos.getY();
  }
}
