package com.wurmcraft.serveressentials.common.modules.protect.utils;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionClaim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionPos;
import com.wurmcraft.serveressentials.common.modules.protect.models.TrustInfo.Action;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ProtectionHelper {

  public static Action[] getActions(BlockPos pos, int dim, EntityPlayer player) {
    return null;
  }

  @Nullable
  public static Claim getClaim(BlockPos pos, int dim) {
    RegionPos region = RegionHelper.getRegionPos(pos, dim);
    try {
      RegionClaim regionData =
          SECore.dataLoader.get(DataType.CLAIM, RegionHelper.convert(region), new RegionClaim());
      return RegionHelper.findClaim(pos, regionData);
    } catch (NoSuchElementException e) {
    } // Claim does not exist
    return null;
  }

  public static void newClaim(Claim claim, int dim) {
    BlockPos center =
        new BlockPos(
            claim.min.x + (claim.max.x - claim.min.x) / 2,
            claim.min.y + (claim.max.y - claim.min.y) / 2,
            claim.min.z + (claim.max.z - claim.min.z) / 2);
    RegionPos region = RegionHelper.getRegionPos(center, dim);
    RegionClaim regionData =
        SECore.dataLoader.get(DataType.CLAIM, RegionHelper.convert(region), new RegionClaim());
    if (regionData != null) {
      List<Claim> regionClaims = Arrays.asList(regionData.claims);
      regionClaims.add(claim);
      regionData.claims = regionClaims.toArray(new Claim[0]);
      SECore.dataLoader.update(DataType.CLAIM, RegionHelper.convert(region), regionData);
    } else {
      regionData = new RegionClaim(RegionHelper.convert(region), region, new Claim[] {claim});
      SECore.dataLoader.register(DataType.CLAIM, RegionHelper.convert(region), regionData);
    }
  }

  public static boolean isAllowed(Claim claim, EntityPlayer player, Action action) {
    return false;
  }

  public static boolean isClaimed(
      BlockPos pos, int dim, int radius, int increment, boolean checkY) {
    if (getClaim(pos, dim) != null) return true;
    if (increment == 0) {
      if (getClaim(pos, dim) != null) return true;
    } else {
      if (!checkY) {
        for (int x = 0; x < radius; x = x + increment)
          for (int z = 0; z < radius; z = z + increment) {
            if (getClaim(pos.add(x, 0, z), dim) != null) return true;
            if (getClaim(pos.add(-x, 0, z), dim) != null) return true;
            if (getClaim(pos.add(x, 0, -z), dim) != null) return true;
            if (getClaim(pos.add(-x, 0, -z), dim) != null) return true;
          }
      } else {
        for (int x = 0; x < radius; x = x + increment)
          for (int z = 0; z < increment; z = z + increment)
            for (int y = 0; y < increment; y = y + increment) {
              if (getClaim(pos.add(x, y, z), dim) != null) return true;
              if (getClaim(pos.add(-x, y, z), dim) != null) return true;
              if (getClaim(pos.add(x, -y, z), dim) != null) return true;
              if (getClaim(pos.add(x, y, -z), dim) != null) return true;
              if (getClaim(pos.add(-x, -y, z), dim) != null) return true;
              if (getClaim(pos.add(-x, y, -z), dim) != null) return true;
              if (getClaim(pos.add(x, -y, -z), dim) != null) return true;
              if (getClaim(pos.add(-x, y, -z), dim) != null) return true;
            }
      }
    }
    return false;
  }
}
