package com.wurmcraft.serveressentials.common.modules.general.event;

import com.google.gson.reflect.TypeToken;
import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.api.models.LastPos;
import com.wurmcraft.serveressentials.api.models.Vault;
import com.wurmcraft.serveressentials.api.models.account.ServerTime;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.ConfigLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.core.ConfigCore;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.modules.general.command.perk.VaultCommand;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
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
  public static NonBlockingHashMap<String, Long> loginTime = new NonBlockingHashMap<>();
  public static NonBlockingHashMap<String, ScheduledFuture<?>> playtimeSync =
      new NonBlockingHashMap<>();
  // AFK Tracking
  public static List<EntityPlayer> afkPlayers = new ArrayList<>();
  public static NonBlockingHashMap<EntityPlayer, LastPos> lastLocation = new NonBlockingHashMap<>();
  public static long afkTime =
      CommandUtils.convertToTime(((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).afkTimer)
          / ((ConfigGeneral) (SECore.moduleConfigs.get("GENERAL"))).afkCheckTimer;

  private static List<EntityPlayer> deadPlayers = new ArrayList<>();
  private static HashMap<String, Location> frozenPlayers = new HashMap<>();

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent e) {
    loginTime.put(e.player.getGameProfile().getId().toString(), System.currentTimeMillis());
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
    if (frozenPlayers == null || frozenPlayers.isEmpty()) loadFreezeFile();
    if(((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).notifyMailboxItems) {
      Vault mailbox = VaultCommand.getVault(e.player.getGameProfile().getId().toString(), "mailbox");
      if ((mailbox != null && mailbox.items != null && mailbox.items.length > 0)) {
        ChatHelper.send(e.player, PlayerUtils.getLang(e.player).MAILBOX_HAS_ITEMS);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void logoutEvent(PlayerEvent.PlayerLoggedOutEvent e) {
    if (e.player != null) {
      updatePlayer(e.player);
      loginTime.remove(e.player.getGameProfile().getId().toString());
      for (String uuid : playtimeSync.keySet()) {
        if (uuid.equalsIgnoreCase(e.player.getGameProfile().getId().toString())) {
          playtimeSync.get(e.player.getGameProfile().getId().toString()).cancel(true);
          playtimeSync.remove(e.player.getGameProfile().getId().toString());
        }
      }
    } else {
      ServerEssentials.LOG.error(
          "Something happened, A Player was unloaded before SE got access to it! GeneralEvents#logoutEvent");
    }
  }

  public void updatePlayer(EntityPlayer player) {
    boolean found = loginTime.contains(player.getGameProfile().getId().toString());
    // Seems to suck at searching for some reason
    for (String text : loginTime.keySet())
      if (text.equalsIgnoreCase(player.getGameProfile().getId().toString())) {
        found = true;
        break;
      }
    if (loginTime != null && found) {
      long lastSyncTime = loginTime.get(player.getGameProfile().getId().toString());
      long time = (System.currentTimeMillis() - lastSyncTime) / 1000;
      time = time / 60;
      Account account = PlayerUtils.getLatestAccount(player.getGameProfile().getId().toString());
      if (account != null) {
        account = addTime(account, time);
        SECore.dataLoader.update(DataLoader.DataType.ACCOUNT, account.uuid, account);
        loginTime.put(player.getGameProfile().getId().toString(), System.currentTimeMillis());
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
    if (!frozenPlayers.isEmpty()
        && frozenPlayers.containsKey(e.player.getGameProfile().getId().toString())) {
      Location lockedPos = frozenPlayers.get(e.player.getGameProfile().getId().toString());
      if (e.player.dimension != lockedPos.dim) {
        TeleportUtils.updateDimension((EntityPlayerMP) e.player, lockedPos);
      }
      if (e.player.getPosition().getX() != lockedPos.x
          || e.player.getPosition().getY() != lockedPos.y
          || e.player.getPosition().getZ() != lockedPos.z) {
        e.player.setPositionAndRotation(
            lockedPos.x, lockedPos.y, lockedPos.z, (float) lockedPos.yaw, (float) lockedPos.pitch);
        e.player.setPositionAndUpdate(
            lockedPos.x, lockedPos.y, lockedPos.z); // Gotta do update, as cannot access directly
      }
      e.player.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 60));
      e.player.addPotionEffect(new PotionEffect(Potion.getPotionById(4), 60));
      e.player.addPotionEffect(new PotionEffect(Potion.getPotionById(18), 60));
      e.player.addPotionEffect(new PotionEffect(Potion.getPotionById(24), 60));
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

  public static void addFrozen(EntityPlayer player, Location pos) {
    if (!frozenPlayers.containsKey(player.getGameProfile().getId().toString())) {
      player.capabilities.disableDamage = true;
      frozenPlayers.put(player.getGameProfile().getId().toString(), pos);
      updateFreezeStorage();
    }
  }

  public static void removeFrozen(EntityPlayer player) {
    if (!frozenPlayers.isEmpty()
        && frozenPlayers.containsKey(player.getGameProfile().getId().toString())) {
      frozenPlayers.remove(player.getGameProfile().getId().toString());
      player.capabilities.disableDamage = false;
      updateFreezeStorage();
    }
  }

  public static void toggleFrozen(EntityPlayer player, Location pos) {
    if (frozenPlayers.containsKey(player.getGameProfile().getId().toString())) {
      removeFrozen(player);
      updateFreezeStorage();
    } else {
      addFrozen(player, pos);
      updateFreezeStorage();
    }
  }

  public static boolean isFrozen(EntityPlayer player) {
    return frozenPlayers.containsKey(player.getGameProfile().getId().toString());
  }

  public static final File freezeFile =
      new File(ConfigLoader.SAVE_DIR + File.separator + "Storage" + File.separator + "freeze.json");

  public static void updateFreezeStorage() {
    HashMap<String, Location> entries = new HashMap<>();
    for (String uuid : frozenPlayers.keySet()) {
      entries.put(uuid, frozenPlayers.get(uuid));
    }
    if (freezeFile.exists()) {
      try {
        Files.write(
            freezeFile.toPath(),
            ServerEssentials.GSON.toJson(entries).getBytes(),
            StandardOpenOption.TRUNCATE_EXISTING);
      } catch (Exception e) {
        ServerEssentials.LOG.error("Failed to write to freeze.txt");
        ServerEssentials.LOG.error(e.getMessage());
      }
    } else {
      try {
        Files.write(
            freezeFile.toPath(),
            ServerEssentials.GSON.toJson(entries).getBytes(),
            StandardOpenOption.CREATE_NEW);
      } catch (Exception e) {
        ServerEssentials.LOG.error("Failed to write to freeze.txt");
        ServerEssentials.LOG.error(e.getMessage());
      }
    }
  }

  public static void loadFreezeFile() {
    try {
      if (freezeFile.exists()) {
        List<String> frozen = Files.readAllLines(freezeFile.toPath());
        frozenPlayers =
            ServerEssentials.GSON.fromJson(
                Strings.join(frozen, "\n"),
                new TypeToken<HashMap<String, Location>>() {}.getType());
        ServerEssentials.LOG.info("Loading '" + frozenPlayers.size() + "' frozen players!");
      }
    } catch (Exception e) {
      ServerEssentials.LOG.error("Failed to load to freeze.txt");
      ServerEssentials.LOG.error(e.getMessage());
    }
  }

  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent e) {
    if (!((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).globalMOTD.isEmpty()) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getServer()
          .getServerStatusResponse()
          .setServerDescription(
              new TextComponentString(
                  formatTitle(((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).globalMOTD)));
    }
  }

  private static String formatTitle(String toBeFormatted) {
    return ChatHelper.replaceColor(
        toBeFormatted.replaceAll(
            "\\{TIME}",
            String.valueOf(
                FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0].getWorldTime()
                    / 3600)));
  }
}
