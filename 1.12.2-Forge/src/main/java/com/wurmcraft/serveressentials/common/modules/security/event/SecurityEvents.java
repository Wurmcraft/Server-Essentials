package com.wurmcraft.serveressentials.common.modules.security.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.security.ConfigSecurity;
import com.wurmcraft.serveressentials.common.modules.security.TrustedList;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public class SecurityEvents {

  public static ConfigSecurity config = ((ConfigSecurity) SECore.moduleConfigs.get(
      "SECURITY"));


  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockEvent.BreakEvent e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlace(BlockEvent.PlaceEvent e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlace(BlockEvent.FarmlandTrampleEvent e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickEmpty e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickItem e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onLeftClick(PlayerInteractEvent.LeftClickBlock e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onLeftClick(PlayerInteractEvent.LeftClickEmpty e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void explosion(ExplosionEvent e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void preventDeath(LivingHurtEvent e) {
    if (config.lockdownEnabled) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onJoinEvent(PlayerLoggedInEvent e) {
    if (config.autoOP && TrustedList.trustedUsers
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
    }
    if (config.checkAlt) {
      HashMap<SocketAddress, UUID> cache = new HashMap<>();
      for (EntityPlayerMP player :
          FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
              .getPlayers()) {
        if (cache.containsKey(player.connection.netManager.getRemoteAddress())) {
          for (EntityPlayerMP p :
              FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()
                  .getPlayers()) {
            if (RankUtils.hasPermission(SECore.dataLoader.get(DataType.ACCOUNT,
                    p.getGameProfile().getId().toString(), new Account()),
                "security.alt.notify")) {
              Language  lang = SECore.dataLoader.get(DataType.LANGUAGE, SECore.dataLoader.get(DataType.ACCOUNT, p.getGameProfile().getId().toString(), new Account()).lang, new Language());
              ChatHelper.send(p, lang.SECURITY_ALT
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
    if (config.modBlacklist != null
        && config.modBlacklist.length > 0) {
      for (String playerMods : NetworkDispatcher
          .get(((EntityPlayerMP) e.player).connection.netManager).getModList().keySet()) {
        for (String blacklist : config.modBlacklist) {
          if (playerMods.equalsIgnoreCase(blacklist)) {
            EntityPlayerMP player = (EntityPlayerMP) e.player;
            Language  lang = SECore.dataLoader.get(DataType.LANGUAGE, SECore.dataLoader.get(DataType.ACCOUNT, player.getGameProfile().getId().toString(), new Account()).lang, new Language());
            player.connection.disconnect(new TextComponentString(
                lang.SECURITY_BLACKLIST
                    .replaceAll("%MOD%", blacklist.toUpperCase())));
            ServerEssentials.LOG
                .warn(player.getName() + "tried to connect with the mod '" + blacklist
                    + "'");
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
