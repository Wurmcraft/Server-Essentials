package com.wurmcraft.serveressentials.common.modules.core.events;

import static com.wurmcraft.serveressentials.ServerEssentials.LOG;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.event.PlayerLoadEvent;
import com.wurmcraft.serveressentials.api.event.PlayerUnloadEvent;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.account.BankAccount;
import com.wurmcraft.serveressentials.api.models.account.ServerTime;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class MainPlayerDataEvents {

  private static final NonBlockingHashMap<String, ScheduledFuture<?>> playerCacheTimeout = new NonBlockingHashMap<>();

  // TODO From Config
  public static final String DEFAULT_RANK = "default";
  public static final String DEFAULT_LANG = "en_us";

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerJoin(PlayerLoggedInEvent e) {
    try {
      Account userAccount =
          SECore.dataLoader.get(
              DataLoader.DataType.ACCOUNT,
              e.getPlayer().getGameProfile().getId().toString(),
              new Account());
      LocalAccount localAccount =
          SECore.dataLoader.get(
              DataLoader.DataType.LOCAL_ACCOUNT,
              e.getPlayer().getGameProfile().getId().toString(),
              new LocalAccount());
      if (userAccount == null || localAccount == null) {
        // Global / Network Account
        boolean user = false;
        if (userAccount == null) {
          userAccount =
              new Account(
                  e.getPlayer().getGameProfile().getId().toString(),
                  e.getPlayer().getDisplayName().getString(),
                  new String[] {DEFAULT_RANK},
                  new String[0],
                  new String[0],
                  SECore.moduleConfigs.get("CORE") != null && !((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang.isEmpty() ? ((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang : DEFAULT_LANG,
                  false,
                  0,
                  "",
                  "",
                  new ServerTime[] {
                      new ServerTime(
                          ServerEssentials.config.general.serverID, 0, Instant.now().getEpochSecond())
                  },
                  new BankAccount[0],
                  0,
                  "",
                  "",
                  new String[0]);
          user =
              SECore.dataLoader.register(
                  DataLoader.DataType.ACCOUNT,
                  e.getPlayer().getGameProfile().getId().toString(),
                  userAccount);
        }
        // Local / Server Account
        boolean local = false;
        if (localAccount == null) {
          localAccount = new LocalAccount(e.getPlayer().getGameProfile().getId().toString());
          local =
              SECore.dataLoader.register(
                  DataLoader.DataType.LOCAL_ACCOUNT,
                  e.getPlayer().getGameProfile().getId().toString(),
                  localAccount);
        }
        handleNewAccount(e.getPlayer(), userAccount, localAccount, user, local);
      } else {
        LOG.info(
            "Loading user '"
                + e.getPlayer().getDisplayName().toString()
                + "' ("
                + e.getPlayer().getGameProfile().getId().toString()
                + ") [Existing]");
        MinecraftForge.EVENT_BUS.post(
            new PlayerLoadEvent(e.getPlayer(), userAccount, localAccount, false));
      }
      // Cancel Cache cleanup if the user has logged back in (relog)
      if (playerCacheTimeout.containsKey(e.getPlayer().getGameProfile().getId().toString())) {
        if (playerCacheTimeout.get(e.getPlayer().getGameProfile().getId().toString()).cancel(false))
          LOG.debug(
              "Canceling Cache cleanup for user '"
                  + e.getPlayer().getDisplayName().toString()
                  + "' ("
                  + e.getPlayer().getGameProfile().getId().toString()
                  + ")");
      }
    } catch (Exception f) {
      f.printStackTrace();
      LOG.warn(
          "Error while attempting to load user '"
              + e.getPlayer().getGameProfile().getId().toString()
              + "'");
    }
  }

  /**
   * Checks if the account was created, printing and triggering the event accordingly
   *
   * @param player player to get the information from
   * @param userAccount newly created local account
   * @param localAccount newly created local account
   * @param user if the account was created
   * @param local if the local account was created
   */
  private void handleNewAccount(
      PlayerEntity player,
      Account userAccount,
      LocalAccount localAccount,
      boolean user,
      boolean local) {
    if (user && local) {
      LOG.info(
          "Loading user '"
              + player.getDisplayName().toString()
              + "' ("
              + player.getGameProfile().getId().toString()
              + ") [New]");
      LOG.info(
          "Created New User ('"
              + player.getGameProfile().getId().toString()
              + "') \""
              + player.getDisplayName().toString()
              + "\"");
      MinecraftForge.EVENT_BUS.post(new PlayerLoadEvent(player, userAccount, localAccount, true));
    } else if (local) {
      LOG.info(
          "Loading user '"
              + player.getDisplayName().toString()
              + "' ("
              + player.getGameProfile().getId().toString()
              + ") [New To Server]");
      LOG.info(
          "Created New Local User ('"
              + player.getGameProfile().getId().toString()
              + "') \""
              + player.getDisplayName().toString()
              + "\"");
      MinecraftForge.EVENT_BUS.post(new PlayerLoadEvent(player, userAccount, localAccount, false));
    } else if (user) {
      LOG.info(
          "Loading user '"
              + player.getDisplayName().toString()
              + "' ("
              + player.getGameProfile().getId().toString()
              + ") [New To Network]");
      LOG.info(
          "Created New Global User ('"
              + player.getGameProfile().getId().toString()
              + "') \""
              + player.getDisplayName().toString()
              + "\"");
      MinecraftForge.EVENT_BUS.post(new PlayerLoadEvent(player, userAccount, localAccount, false));
    } else LOG.warn("Failed to create new user");
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onPlayerLogout(PlayerLoggedOutEvent e) {
    LOG.info(
        "Scheduling unloading for user '"
            + e.getPlayer().getDisplayName().toString()
            + "' ("
            + e.getPlayer().getGameProfile().getId().toString()
            + ") in "
            + ServerEssentials.config.performance.playerCacheTimeout
            + "s");
    Runnable scheduledTask =
        () -> {
          String uuid = e.getPlayer().getGameProfile().getId().toString();
          Account account = SECore.dataLoader.get(DataLoader.DataType.ACCOUNT, uuid, new Account());
          LocalAccount local =
              SECore.dataLoader.get(DataLoader.DataType.LOCAL_ACCOUNT, uuid, new LocalAccount());
          if (!PlayerUtils.isUserOnline(uuid)
              && SECore.dataLoader.delete(DataLoader.DataType.ACCOUNT, uuid, true)
              && SECore.dataLoader.delete(DataLoader.DataType.LOCAL_ACCOUNT, uuid, true)) {
            MinecraftForge.EVENT_BUS.post(new PlayerUnloadEvent(account, local));
            LOG.info(
                "Unloading user '"
                    + UsernameCache.getLastKnownUsername(UUID.fromString(uuid))
                    + "' ("
                    + uuid
                    + ")");
          }
        };
    ScheduledFuture<?> future =
        ServerEssentials.scheduledService.schedule(
            scheduledTask,
            ServerEssentials.config.performance.playerCacheTimeout,
            TimeUnit.SECONDS);
    playerCacheTimeout.put(e.getPlayer().getGameProfile().getId().toString(), future);
  }
}
