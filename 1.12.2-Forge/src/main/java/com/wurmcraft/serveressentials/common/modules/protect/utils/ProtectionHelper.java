package com.wurmcraft.serveressentials.common.modules.protect.utils;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionClaim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionPos;
import com.wurmcraft.serveressentials.common.modules.protect.models.TrustInfo.Action;
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
      RegionClaim regionData = SECore.dataLoader.get(DataType.CLAIM,
          RegionHelper.convert(region), new RegionClaim());
      return RegionHelper.findClaim(pos, regionData);
    } catch (NoSuchElementException e) {
    } // Claim does not exist
    return null;
  }

  public static boolean isAllowed(Claim claim, EntityPlayer player, Action action) {
    return false;
  }
}
