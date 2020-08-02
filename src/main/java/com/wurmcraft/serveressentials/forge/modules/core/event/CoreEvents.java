package com.wurmcraft.serveressentials.forge.modules.core.event;

import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class CoreEvents {

  public static NonBlockingHashMap<UUID, LocationWrapper> moveTracker = new NonBlockingHashMap<>();

  @SubscribeEvent
  public void livingUpdate(LivingUpdateEvent e) {
    if (e.getEntityLiving() instanceof EntityPlayer && !moveTracker.isEmpty()
        && moveTracker
        .containsKey(((EntityPlayer) e.getEntityLiving()).getGameProfile().getId())) {
      EntityPlayer player = (EntityPlayer) e.getEntityLiving();
      LocationWrapper savedPos = moveTracker.get(player.getGameProfile().getId());
      if (savedPos.x != player.posX || savedPos.y != player.posY
          || savedPos.z != player.posZ || savedPos.dim != player.dimension) {
        moveTracker.remove(player.getGameProfile().getId());
        ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).COMMAND_MOVED);
      }
    }
  }
}
