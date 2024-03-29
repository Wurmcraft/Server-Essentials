package com.wurmcraft.serveressentials.common.modules.general.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.LastPos;
import com.wurmcraft.serveressentials.api.models.account.ServerTime;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class GeneralEvents {

  // Time Tracking
  public static NonBlockingHashMap<UUID, Long> loginTime = new NonBlockingHashMap<>();
  public static NonBlockingHashMap<String, ScheduledFuture<?>> playtimeSync =
      new NonBlockingHashMap<>();
  // AFK Tracking
  public static List<EntityPlayer> afkPlayers = new ArrayList<>();
  public static NonBlockingHashMap<EntityPlayer, LastPos> lastLocation = new NonBlockingHashMap<>();
  public static long afkTime =
      CommandUtils.convertToTime(((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).afkTimer)
          / ((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).afkCheckTimer;

  private static List<EntityPlayer> deadPlayers = new ArrayList<>();
  private static HashMap<EntityPlayer, BlockPos> frozenPlayers = new HashMap<>();

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent e) {
    loginTime.put(e.player.getGameProfile().getId(), System.currentTimeMillis());
    ScheduledFuture<?> future =
        ServerEssentials.scheduledService.scheduleAtFixedRate(
            () -> {
              updatePlayer(e.player);
            },
            (CommandUtils.convertToTime(
                ((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).playTimeSync)),
            (CommandUtils.convertToTime(
                ((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).playTimeSync)),
            TimeUnit.SECONDS);
    playtimeSync.put(e.player.getGameProfile().getId().toString(), future);
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void logoutEvent(PlayerEvent.PlayerLoggedOutEvent e) {
    if (e.player != null) {
      updatePlayer(e.player);
      loginTime.remove(e.player.getGameProfile().getId());
      if (playtimeSync.contains(e.player.getGameProfile().getId().toString())) {
        playtimeSync.get(e.player.getGameProfile().getId().toString()).cancel(true);
        playtimeSync.remove(e.player.getGameProfile().getId().toString());
      }
    } else {
      ServerEssentials.LOG.error(
          "Something happened, A Player was unloaded before SE got access to it! GeneralEvents#logoutEvent");
    }
  }

  public void updatePlayer(EntityPlayer player) {
    if (loginTime != null && loginTime.contains(player.getGameProfile().getId())) {
      long lastSyncTime = loginTime.get(player.getGameProfile().getId());
      long time = (System.currentTimeMillis() - lastSyncTime) / 1000;
      time = time / 60;
      Account account = PlayerUtils.getLatestAccount(player.getGameProfile().getId().toString());
      if (account != null) {
        account = addTime(account, time);
        SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, account.uuid, account);
        loginTime.put(player.getGameProfile().getId(), System.currentTimeMillis());
      }
    } else {
      //      ServerEssentials.LOG.warn(
      //          "Unable to save player playtime! loginTime does not contain the player or the
      // player is null! '"
      //              + (player == null)
      //              + "'");
    }
  }

  public Account addTime(Account account, long time) {
    if (account.tracked_time != null) {
      for (int index = 0; index < account.tracked_time.length; index++) {
        if (account.tracked_time[index].serverID.equalsIgnoreCase(
            ServerEssentials.config.general.serverID)) {
          account.tracked_time[index].totalTime = account.tracked_time[index].totalTime + time;
          account.tracked_time[index].lastSeen = Instant.now().getEpochSecond();
          return account;
        }
      }
    }
    ServerTime stat =
        new ServerTime(
            ServerEssentials.config.general.serverID, time, Instant.now().getEpochSecond());
    if (account.tracked_time == null) {
      account.tracked_time = new ServerTime[0];
    }
    account.tracked_time = Arrays.copyOf(account.tracked_time, account.tracked_time.length + 1);
    account.tracked_time[account.tracked_time.length - 1] = stat;
    return account;
  }

  @SubscribeEvent
  public void onPlayerMove(TickEvent.PlayerTickEvent e) {
    if (e.player.world.getWorldTime()
            % (((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).afkCheckTimer * 20L)
        == 0) {
      if (lastLocation.containsKey(e.player)) {
        Location loc = lastLocation.get(e.player).location;
        if (loc.x == e.player.posX
            && loc.y == e.player.posY
            && loc.z == e.player.posZ
            && loc.dim == e.player.dimension) {
          LastPos pos = lastLocation.get(e.player);
          pos.increment();
          lastLocation.put(e.player, pos);
          if (pos.checker > afkTime) {
            afk(e.player, true);
          }
        } else {
          if (afkPlayers.contains(e.player)) {
            afk(e.player, false);
          }
          lastLocation.put(
              e.player,
              new LastPos(
                  new Location(
                      e.player.posX,
                      e.player.posY,
                      e.player.posZ,
                      e.player.dimension,
                      e.player.rotationPitch,
                      e.player.rotationYaw)));
        }
      } else {
        lastLocation.put(
            e.player,
            new LastPos(
                new Location(
                    e.player.posX,
                    e.player.posY,
                    e.player.posZ,
                    e.player.dimension,
                    e.player.rotationPitch,
                    e.player.rotationYaw)));
      }
    }
    if (frozenPlayers.size() > 0 && frozenPlayers.keySet().contains(e.player)) {
      BlockPos lockedPos = frozenPlayers.get(e.player);
      if (e.player.getPosition() != lockedPos) {
        e.player.setPositionAndUpdate(lockedPos.getX(), lockedPos.getY(), lockedPos.getZ());
      }
    }
  }

  @SubscribeEvent
  public void onContainer(PlayerContainerEvent.Open e) {
    if (afkPlayers.contains(e.getEntityPlayer())) {
      afk(e.getEntityPlayer(), false);
    }
  }

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock e) {
    if (afkPlayers.contains(e.getEntityPlayer())) {
      afk(e.getEntityPlayer(), false);
    }
  }

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickItem e) {
    if (afkPlayers.contains(e.getEntityPlayer())) {
      afk(e.getEntityPlayer(), false);
    }
  }

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.LeftClickBlock e) {
    if (afkPlayers.contains(e.getEntityPlayer())) {
      afk(e.getEntityPlayer(), false);
    }
  }

  @SubscribeEvent
  public void playerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent e) {
    afkPlayers.remove(e.player);
    lastLocation.remove(e.player);
  }

  public static void afk(EntityPlayer player, boolean afk) {
    Language commandLang =
        SECore.dataLoader.get(
            DataLoader.DataType.LANGUAGE,
            ((ConfigCore) SECore.moduleConfigs.get("CORE")).defaultLang,
            new Language());
    if (afk) {
      if (!afkPlayers.contains(player)) {
        afkPlayers.add(player);
        ServerEssentials.LOG.info(
            commandLang.ANNOUNCEMENT_AFK_ENABLED.replaceAll(
                "\\{@NAME@}", player.getDisplayNameString()));
        for (EntityPlayer randPlayer :
            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
          Language lang =
              SECore.dataLoader.get(
                  DataLoader.DataType.LANGUAGE,
                  SECore.dataLoader.get(
                          DataLoader.DataType.ACCOUNT,
                          player.getGameProfile().getId().toString(),
                          new Account())
                      .lang,
                  new Language());
          ChatHelper.send(
              randPlayer,
              lang.ANNOUNCEMENT_AFK_ENABLED.replaceAll(
                  "\\{@NAME@}", player.getDisplayNameString()));
        }
      }
    } else {
      afkPlayers.remove(player);
      ServerEssentials.LOG.info(
          commandLang.ANNOUNCEMENT_AFK_DISABLED.replaceAll(
              "\\{@NAME@}", player.getDisplayNameString()));
      for (EntityPlayer randPlayer :
          FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
        Language lang =
            SECore.dataLoader.get(
                DataLoader.DataType.LANGUAGE,
                SECore.dataLoader.get(
                        DataLoader.DataType.ACCOUNT,
                        player.getGameProfile().getId().toString(),
                        new Account())
                    .lang,
                new Language());
        ChatHelper.send(
            randPlayer,
            lang.ANNOUNCEMENT_AFK_DISABLED.replaceAll("\\{@NAME@}", player.getDisplayNameString()));
      }
    }
  }

  @SubscribeEvent
  public void onLivingDeath(LivingDeathEvent e) {
    if (e.getEntityLiving() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntityLiving();
      Account account =
          SECore.dataLoader.get(
              DataType.ACCOUNT, player.getGameProfile().getId().toString(), new Account());
      if (RankUtils.hasPermission(account, "general.back.death")) {
        LocalAccount playerData =
            SECore.dataLoader.get(
                DataType.LOCAL_ACCOUNT,
                player.getGameProfile().getId().toString(),
                new LocalAccount());
        playerData.lastLocation =
            new Location(
                player.posX,
                player.posY,
                player.posZ,
                player.dimension,
                player.cameraPitch,
                player.cameraYaw);
        SECore.dataLoader.register(DataType.LOCAL_ACCOUNT, playerData.uuid, playerData);
      }
      if (e.getEntityLiving() instanceof EntityPlayer) {
        deadPlayers.add((EntityPlayer) e.getEntityLiving());
      }
    }
  }

  public static void addFrozen(EntityPlayer player, BlockPos pos) {
    if (!frozenPlayers.keySet().contains(player)) {
      player.capabilities.disableDamage = true;
      frozenPlayers.put(player, pos);
    }
  }

  public static void removeFrozen(EntityPlayer player) {
    if (frozenPlayers.size() > 0 && frozenPlayers.keySet().contains(player)) {
      frozenPlayers.remove(player);
      player.capabilities.disableDamage = false;
    }
  }

  public static void toggleFrozen(EntityPlayer player, BlockPos pos) {
    if (frozenPlayers.keySet().contains(player)) {
      removeFrozen(player);
    } else {
      addFrozen(player, pos);
    }
  }

  public static boolean isFrozen(EntityPlayer player) {
    return frozenPlayers.keySet().contains(player);
  }
}
