package com.wurmcraft.serveressentials.common.utils;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PlayerUtils {

  public static boolean isUserOnline(String uuid) {
    for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
      if (player.getGameProfile().getId().toString().equals(uuid)) {
        return true;
      }
    }
    return false;
  }

}
