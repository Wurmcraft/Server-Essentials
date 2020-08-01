package com.wurmcraft.serveressentials.forge.modules.general.utils;

import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class GeneralUtils {

  public static List<Object[]> requestingTPA = new ArrayList<>();

  public static int getMaxHomes(EntityPlayer player) {
    StoredPlayer playerData = PlayerUtils.get(player);
    int total = GeneralModule.config.defaultHomeCount;
    return total;
  }

  public static boolean hasActiveTPARequest(EntityPlayer player) {
    for (Object[] data : requestingTPA) {
      if (((EntityPlayer) data[1]).getGameProfile().getId()
          .equals(player.getGameProfile().getId())) {
        return true;
      }
    }
    return false;
  }

  public static Object[] getActiveTPARequest(EntityPlayer player) {
    List<Integer> timeout = new ArrayList<>();
    for (int index = 0; index < requestingTPA.size(); index++) {
      Object[] data = requestingTPA.get(index);
      long timeoutTime = (long) data[0];
      if (timeoutTime >= System.currentTimeMillis()) {
        if (((EntityPlayer) data[1]).getGameProfile().getId()
            .equals(player.getGameProfile().getId())) {
          handleTimeout(timeout);
          return new Object[]{data[1], data[2]};
        }
      } else {
        timeout.add(index);
      }
    }
    handleTimeout(timeout);
    return null;
  }

  public static void handleTimeout(List<Integer> timeout) {
    for (int x : timeout) {
      requestingTPA.remove(x);
    }
  }
}
