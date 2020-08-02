package com.wurmcraft.serveressentials.forge.modules.general.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class GeneralEvents {

  private static List<String> sentToSpawn = new ArrayList<>();

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent e) {
    if (e.getEntityLiving() instanceof EntityPlayer) {
      if (RankUtils.hasPermission(e.getEntityLiving(), "general.back.death")) {
        EntityPlayer player = (EntityPlayer) e.getEntityLiving();
        StoredPlayer playerData = PlayerUtils.get(player);
        playerData.server.lastLocation = new LocationWrapper(player.posX, player.posY,
            player.posZ, player.dimension);
        SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void newPlayer(NewPlayerEvent e) {
    if (e.newData != null && SECore.config.spawn != null && !sentToSpawn.contains(e.newData.uuid)) {
      for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getPlayerList().getPlayers()) {
        if (player.getGameProfile().getId().toString().equals(e.newData.uuid)) {
          sentToSpawn.add(player.getGameProfile().getId().toString());
          TeleportUtils.teleportTo(player, SECore.config.spawn);
          return;
        }
      }
    }
  }
}
