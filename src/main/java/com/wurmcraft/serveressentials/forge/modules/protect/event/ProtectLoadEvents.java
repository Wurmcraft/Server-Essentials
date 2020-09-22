package com.wurmcraft.serveressentials.forge.modules.protect.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.modules.protect.data.ClaimData;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.RegionHelper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ProtectLoadEvents {

  public HashMap<UUID, BlockPos> playerPos = new HashMap<>();
  public HashMap<UUID, ClaimData> playerData = new HashMap<>();

  @SubscribeEvent
  public void onChunkLoad(ChunkEvent.Load e) {
    String regionID = RegionHelper.getIDForRegion(e.getChunk());
    try {
      SECore.dataHandler.getData(DataKey.CLAIM, regionID);
    } catch (NoSuchElementException ignored) {

    }
  }

//  @SubscribeEvent
//  public void onChunkLoad(ChunkEvent.Unload e) {
//    String regionID = RegionHelper.getIDForRegion(e.getChunk());
//    try {
//      SECore.dataHandler.delData(DataKey.CLAIM, regionID, false);
//    } catch (NoSuchElementException ignored) {
//
//    }
//  }

  @SubscribeEvent
  public void onPlayerEnter(LivingUpdateEvent e) {
    if (e.getEntityLiving() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntityLiving();
      if (playerPos.containsKey(player.getGameProfile().getId()) && !playerPos
          .get(player.getGameProfile().getId()).equals(player.getPosition()) || !playerPos
          .containsKey(player.getGameProfile().getId())) {
        ClaimData data = ProtectionHelper
            .getClaim(player.getPosition(), player.dimension);
        if(data == null && playerData.containsKey(player.getGameProfile().getId()) && playerData.get(player.getGameProfile().getId()) != null) {
          ChatHelper.sendMessage(player, TextFormatting.GREEN + "You have entered the wilderness");
        } else if (data != null && playerData.containsKey(player.getGameProfile().getId())) {
          ClaimData oldData = playerData.get(player.getGameProfile().getId());
          if (oldData == null) {
            ChatHelper.sendMessage(player,
                TextFormatting.RED + "You have entered " + UsernameCache
                    .getLastKnownUsername(UUID.fromString(data.owner)) + "'s Claim");
          } else if (!oldData.owner.equals(data.owner)) {
            ChatHelper.sendMessage(player,
                TextFormatting.RED + "You have entered " + UsernameCache
                    .getLastKnownUsername(UUID.fromString(data.owner)) + "'s Claim");
          }
        }
        playerPos.put(player.getGameProfile().getId(), player.getPosition());
        playerData.put(player.getGameProfile().getId(), data);
      }

    }
  }
}
