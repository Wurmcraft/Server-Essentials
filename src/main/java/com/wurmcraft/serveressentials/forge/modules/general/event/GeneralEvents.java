package com.wurmcraft.serveressentials.forge.modules.general.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GeneralEvents {

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
}
