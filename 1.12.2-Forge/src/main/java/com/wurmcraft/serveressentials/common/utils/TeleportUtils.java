package com.wurmcraft.serveressentials.common.utils;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.world.WorldServer;

public class TeleportUtils {

  public static boolean teleportTo(EntityPlayerMP player, LocalAccount local, Location location) {
    return teleportTo(player, local, location, true);
  }

  public static boolean teleportTo(
      EntityPlayerMP player, LocalAccount local, Location location, boolean checkTimer) {
    if (local.teleportTimer >= System.currentTimeMillis()) {
      return false;
    }
    // Update Teleport info
    local.teleportTimer = System.currentTimeMillis() + 1000;
    local.lastLocation =
        new Location(
            player.posX,
            player.posY,
            player.posZ,
            player.dimension,
            player.rotationPitch,
            player.rotationYaw);
    // Check for dimension change
    if (player.dimension == location.dim) {
      player.connection.setPlayerLocation(
          location.x, location.y, location.z, (float) location.pitch, (float) location.yaw);
    } else { // Update Player's dimension
      // Remove from existing world
      int oldDim = player.dimension;
      player.dimension = location.dim;
      WorldServer serverWorld = player.mcServer.getWorld(location.dim);
      player.connection.sendPacket(
          new SPacketRespawn(
              location.dim,
              serverWorld.getDifficulty(),
              serverWorld.getWorldInfo().getTerrainType(),
              player.interactionManager.getGameType()));
      player.mcServer.getWorld(oldDim).removeEntityDangerously(player);
      player.isDead = false;
      // Spawn into other dimension
      if (player.isEntityAlive()) {
        serverWorld.spawnEntity(player);
        serverWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(serverWorld);
      }
      player.mcServer.getPlayerList().preparePlayer(player, serverWorld);
      player.connection.setPlayerLocation(
          location.x, location.y, location.z, (float) location.pitch, (float) location.yaw);
      player.interactionManager.setWorld(serverWorld);
    }
    SECore.dataLoader.update(DataLoader.DataType.LOCAL_ACCOUNT, local.uuid, local);
    return true;
  }
}
