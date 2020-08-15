package com.wurmcraft.serveressentials.forge.modules.core.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.config.GlobalConfig;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.modules.core.CoreModule;
import com.wurmcraft.serveressentials.forge.server.json.MOTDSettings;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse.Version;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class CoreEvents {

  public static NonBlockingHashMap<UUID, LocationWrapper> moveTracker = new NonBlockingHashMap<>();
  public static MOTDSettings motdSettings = CoreModule.messagesConfig.motd;
  public static int currentSelection;
  public static long nextSwap = 0;
  public static Random random = new Random();

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

  @SubscribeEvent
  public void serverTick(TickEvent.ServerTickEvent e) {
    if(SECore.config.enableCustomMOTD) {
      if (e.phase != TickEvent.Phase.START) {
        return;
      }
      ServerStatusResponse ssr = FMLCommonHandler.instance()
          .getMinecraftServerInstance().getServerStatusResponse();
      ssr.setServerDescription(
          new TextComponentString(
              motdSettings.onlineMOTD[currentSelection].replaceAll("&", "\u00a7")));
      if (nextSwap <= System.currentTimeMillis() && motdSettings.onlineMOTD.length > 1) {
        currentSelection = random.nextInt(motdSettings.onlineMOTD.length);
        nextSwap = System.currentTimeMillis() + (motdSettings.onlineMOTDswapTime * 1000);
      }
    }
  }
}
