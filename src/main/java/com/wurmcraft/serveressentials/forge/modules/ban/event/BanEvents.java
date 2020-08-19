package com.wurmcraft.serveressentials.forge.modules.ban.event;

import com.wurmcraft.serveressentials.forge.api.json.rest.GlobalBan;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class BanEvents {

  @SubscribeEvent
  public void onPlayerJoin(PlayerLoggedInEvent e) {
    checkPlayer((EntityPlayerMP) e.player);
  }

  public static void checkPlayer(EntityPlayerMP player) {
    ServerEssentialsServer.EXECUTORS.schedule(
        () -> {
          GlobalBan[] bans = RestRequestHandler.Ban.getGlobalBans();
          for (GlobalBan b : bans) {
            if (b.uuid.equalsIgnoreCase(player.getGameProfile().getId().toString())
                || b.ip
                .equals(player.connection.netManager.getRemoteAddress().toString())) {
              player.connection.disconnect(new TextComponentString(
                  TextFormatting.RED + "You have been banned for '" + b.reason + "'"));
              ServerEssentialsServer.LOGGER.info(
                  player.getDisplayNameString() + " tried to connect but is banned!, ("
                      + b.reason + ")");
            }
          }
        },
        0,
        TimeUnit.SECONDS);
  }

}
