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
    ClaimData claim = getClaim(pos, dim);
    if (claim == null ||  isTrusted(claim, player)) {
      return Action.values();
    }
    return new Action[0];
  }

  public static ClaimData getClaim(BlockPos pos, int dim) {
    String regionID = RegionHelper.getIDForRegion(pos, dim);
    try {
      RegionClaimData data = (RegionClaimData) SECore.dataHandler
          .getData(DataKey.CLAIM, regionID);
      for (ClaimData claim : data.claims) {
        if (isWithin(claim, pos)) {
          return claim;
        }
      }
    } catch (NoSuchElementException ignored) {
    }
    return null;
  }

  public static void saveClaimData(ClaimData data, int dim) {
    String regionID = RegionHelper.getIDForRegion(data.lowerPos.toBlockPos(), dim);
    try {
      RegionClaimData regionData = (RegionClaimData) SECore.dataHandler
          .getData(DataKey.CLAIM, regionID);
      for (int index = 0; index < regionData.claims.length; index++) {
        if (regionData.claims[index].lowerPos.equals(data.lowerPos)
            && regionData.claims[index].higherPos.equals(data.higherPos)) {
          regionData.claims[index] = data;
          break;
        }
      }
      SECore.dataHandler.registerData(DataKey.CLAIM, regionData);
    } catch (NoSuchElementException ignored) {
    }
  }

  public static boolean isTrusted(ClaimData data, EntityPlayer player) {
    if (player == null) {
      return false;
    }
    if( data.owner.equals(player.getGameProfile().getId().toString()) )
      return true;
    for(String uuid : data.trusted) {
      if(uuid.equalsIgnoreCase(player.getGameProfile().getId().toString()))
        return true;
    }
    return false;
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
        (claim.lowerPos.y <= pos.getY() && claim.higherPos.y >= pos.getY()) &&
        (claim.lowerPos.z <= pos.getZ() &&
            claim.higherPos.z >= pos.getZ());
  }

  public enum Action {
    BREAK, PLACE, LEFT_CLICK, LEFT_CLICK_EMPTY, RIGHT_CLICK, RIGHT_CLICK_EMPTY, EXPLOSION
  }


}
