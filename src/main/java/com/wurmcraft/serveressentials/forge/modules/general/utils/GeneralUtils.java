package com.wurmcraft.serveressentials.forge.modules.general.utils;

import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

public class GeneralUtils {

  public static int getMaxHomes(EntityPlayer player) {
    StoredPlayer playerData = PlayerUtils.get(player);
    int total = GeneralModule.config.defaultHomeCount;
    return total;
  }

}
