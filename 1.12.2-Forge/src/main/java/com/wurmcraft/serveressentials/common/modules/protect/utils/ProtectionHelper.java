package com.wurmcraft.serveressentials.common.modules.protect.utils;

import com.wurmcraft.serveressentials.api.models.Action;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.RegionPos;
import java.util.NoSuchElementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ProtectionHelper {

  public static Action[] getActions(BlockPos pos, int dim, EntityPlayer player) {
    return null;
  }

  public static Claim getClaim(BlockPos pos, int dim) {
    RegionPos region = RegionHelper.getRegionPos(pos, dim);
    try {
//      RegionClaims regionData = SECore.dataLoader.get(DataType.CLAIM,)
    } catch (NoSuchElementException e) {} // Claim does not exist
    return null;
  }
}
