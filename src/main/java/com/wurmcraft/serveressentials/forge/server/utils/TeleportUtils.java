package com.wurmcraft.serveressentials.forge.server.utils;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TeleportUtils {

  public static void teleportTo(EntityPlayer player, LocationWrapper loc) {
    StoredPlayer playerData = PlayerUtils.get(player);
    playerData.server.teleportTimer = System.currentTimeMillis();
    playerData.server.lastLocation = new LocationWrapper(player.posX, player.posY,
        player.posZ, player.dimension);
    teleportTo((EntityPlayerMP) player, loc);
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
  }

  public static boolean teleportTo(
      EntityPlayerMP player, LocationWrapper location) {
    if (player.dimension != location.dim) {
      int saveDim = player.dimension;
      WorldServer newWorld = player.mcServer.getWorld(location.dim);
      player.dimension = location.dim;
      player.connection.sendPacket(
          new SPacketRespawn(
              location.dim,
              newWorld.getDifficulty(),
              newWorld.getWorldInfo().getTerrainType(),
              player.interactionManager.getGameType()));
      player.mcServer.getWorld(saveDim).removeEntityDangerously(player);
      player.isDead = false;
      if (player.isEntityAlive()) {
        newWorld.spawnEntity(player);
        player.setLocationAndAngles(
            location.x + .5,
            location.y + 1,
            location.z + .5,
            player.rotationYaw,
            player.rotationPitch);
        newWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(newWorld);
      }
      player.mcServer.getPlayerList()
          .preparePlayer(player, player.mcServer.getWorld(saveDim));
      player.connection.setPlayerLocation(
          location.x,
          location.y,
          location.z,
          player.rotationYaw,
          player.rotationPitch);
      player.interactionManager.setWorld(newWorld);
      player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, newWorld);
      player.mcServer.getPlayerList().syncPlayerInventory(player);
      for (PotionEffect potioneffect : player.getActivePotionEffects()) {
        player.connection
            .sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
      }
      player.connection.sendPacket(
          new SPacketSetExperience(
              player.experience, player.experienceTotal, player.experienceLevel));
      FMLCommonHandler.instance()
          .firePlayerChangedDimensionEvent(player, saveDim, location.dim);
    } else {
      player.setPositionAndUpdate(location.x, location.y, location.z);
    }
    return true;
  }
}