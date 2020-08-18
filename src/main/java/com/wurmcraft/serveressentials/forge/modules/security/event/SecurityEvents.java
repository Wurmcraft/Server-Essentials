package com.wurmcraft.serveressentials.forge.modules.security.event;

import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.modules.security.SecurityModule;
import com.wurmcraft.serveressentials.forge.server.ServerEssentialsServer;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public class SecurityEvents {

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onJoinEvent(PlayerLoggedInEvent e) {
    if (SecurityModule.config.autoOP && SecurityModule.trustedUsers
        .contains(e.player.getGameProfile().getId().toString())) {
      if (!isOp(e.player)) {
        FMLCommonHandler.instance()
            .getMinecraftServerInstance()
            .getCommandManager()
            .executeCommand(
                FMLCommonHandler.instance().getMinecraftServerInstance(),
                "op " + UsernameCache
                    .getLastKnownUsername(e.player.getGameProfile().getId()));
      }
      ChatHelper.sendMessage(
          e.player, PlayerUtils.getLanguage(e.player).SECURITY_TRUSTED);
    }
    if (SecurityModule.config.checkAlt) {
      HashMap<SocketAddress, UUID> cache = new HashMap<>();
      for (EntityPlayerMP player :
          FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
              .getPlayers()) {
        if (cache.containsKey(player.connection.netManager.getRemoteAddress())) {
          for (EntityPlayerMP p :
              FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                  .getPlayers()) {
            if (RankUtils.hasPermission(p, "security.alt.notify")) {
              ChatHelper.sendMessage(p, PlayerUtils.getLanguage(p).SECURITY_ALT
                  .replaceAll("%PLAYER%", player.getDisplayNameString())
                  .replaceAll("%PLAYER2%",
                      player.connection.netManager.getRemoteAddress().toString()));
            }
          }
        } else {
          cache.put(player.connection.netManager.getRemoteAddress(),
              player.getGameProfile().getId());
        }
      }
    }
    if (SecurityModule.config.modBlacklist != null
        && SecurityModule.config.modBlacklist.length > 0) {
      for (String playerMods : NetworkDispatcher
          .get(((EntityPlayerMP) e.player).connection.netManager).getModList().keySet()) {
        for (String blacklist : SecurityModule.config.modBlacklist) {
          if (playerMods.equalsIgnoreCase(blacklist)) {
            EntityPlayerMP player = (EntityPlayerMP) e.player;
            player.connection.disconnect(new TextComponentString(
                PlayerUtils.getLanguage(null).SECURITY_BLACKLIST
                    .replaceAll("%MOD%", blacklist.toUpperCase())));
            for (EntityPlayerMP p :
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                    .getPlayers()) {
              ChatHelper
                  .sendMessage(p, PlayerUtils.getLanguage(p).SECURITY_BLACKLIST_CONNECT);
            }
            ServerEssentialsServer.LOGGER
                .warn(player.getName() + "tried to connect with the mod " + blacklist);
          }
        }
      }
    }
  }

  private static boolean isOp(EntityPlayer player) {
    return FMLCommonHandler.instance()
        .getMinecraftServerInstance()
        .getPlayerList()
        .getOppedPlayers()
        .getPermissionLevel(player.getGameProfile())
        > 0;
  }

}
