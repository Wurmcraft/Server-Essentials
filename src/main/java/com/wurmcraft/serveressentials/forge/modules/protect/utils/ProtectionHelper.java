package com.wurmcraft.serveressentials.forge.modules.protect.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.modules.protect.data.ClaimData;
import com.wurmcraft.serveressentials.forge.modules.protect.data.RegionClaimData;
import java.util.Arrays;
import java.util.NoSuchElementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ProtectionHelper {

  public static Action[] getActionList(BlockPos pos, int dim, EntityPlayer player) {
    String regionID = RegionHelper.getIDForRegion(pos, dim);
    try {
      RegionClaimData data = (RegionClaimData) SECore.dataHandler
          .getData(DataKey.CLAIM, regionID);
      for (ClaimData claim : data.claims) {
        if (isWithin(claim, pos)) {
          return new Action[0];
        }
      }
      return Action.values();
    } catch (NoSuchElementException e) {
      return Action.values();
    }
  }

  public static boolean test(Action action, Action[] actionList) {
    return Arrays.stream(actionList).anyMatch(a -> action == a);
  }

  public static boolean test(Action action, BlockPos pos, int dim, EntityPlayer player) {
    return test(action, getActionList(pos, dim, player));
  }

  public static boolean isWithin(ClaimData claim, BlockPos pos) {
    return (claim.lowerPos.x <= pos.getX() &&
        claim.higherPos.x >= pos.getX()) &&
        (claim.lowerPos.y <= pos.getY() && claim.higherPos.y >= pos.getZ()) &&
        (claim.lowerPos.z <= pos.getZ() &&
            claim.higherPos.z >= pos.getZ());
  }

  public enum Action {
    BREAK, PLACE, LEFT_CLICK, LEFT_CLICK_EMPTY, RIGHT_CLICK, RIGHT_CLICK_EMPTY, EXPLOSION
  }


}
