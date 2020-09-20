package com.wurmcraft.serveressentials.forge.modules.general.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.player.Home;
import com.wurmcraft.serveressentials.forge.api.json.player.NameLookup;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.modules.general.utils.GeneralUtils;
import com.wurmcraft.serveressentials.forge.modules.general.utils.PlayerInventory;
import com.wurmcraft.serveressentials.forge.modules.rank.RankModule;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.data.RestRequestHandler;
import com.wurmcraft.serveressentials.forge.server.events.DataBaseCommandPath;
import com.wurmcraft.serveressentials.forge.server.loader.ModuleLoader;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.cliffc.high_scale_lib.NonBlockingHashSet;

public class GeneralEvents {

  private static List<String> sentToSpawn = new ArrayList<>();
  private static HashMap<EntityPlayer, PlayerInventory> openInv = new HashMap<>();
  private static HashMap<EntityPlayer, BlockPos> frozenPlayers = new HashMap<>();
  private static List<EntityPlayer> deadPlayers = new ArrayList<>();
  public static NonBlockingHashSet<EntityPlayer> vanishedPlayers = new NonBlockingHashSet<>();

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
      if (e.getEntityLiving() instanceof EntityPlayer) {
        deadPlayers.add((EntityPlayer) e.getEntityLiving());
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void newPlayer(NewPlayerEvent e) {
    if (e.newData != null && getNewPlayerSpawn() != null && !sentToSpawn
        .contains(e.newData.uuid)) {
      for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getPlayerList().getPlayers()) {
        if (player.getGameProfile().getId().toString().equals(e.newData.uuid)) {
          sentToSpawn.add(player.getGameProfile().getId().toString());
          TeleportUtils.teleportTo(player, getNewPlayerSpawn());
          return;
        }
      }
    }
  }

  private LocationWrapper getNewPlayerSpawn() {
    if (GeneralModule.config != null && GeneralModule.config.spawn != null) {
      if (GeneralModule.config.spawn.spawns.get("firstJoin") != null) {
        return GeneralModule.config.spawn.spawns.get("firstJoin");
      } else if (ModuleLoader.getLoadedModule("Rank") != null
          && GeneralModule.config.spawn.spawns.get(RankModule.config.defaultRank)
          != null) {
        return GeneralModule.config.spawn.spawns.get(RankModule.config.defaultRank);
      } else if (GeneralModule.config.spawn.spawns.get("spawn") != null) {
        return GeneralModule.config.spawn.spawns.get("spawn");
      }
    }
    return null;
  }

  @SubscribeEvent
  public void onPlayerJoin(PlayerLoggedInEvent e) {
    if (GeneralModule.config != null && GeneralModule.config.displayMOTDOnJoin) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getCommandManager()
          .executeCommand(e.player, "/motd");
    }
    if(SECore.config.dataStorageType.equalsIgnoreCase("Rest")) {
      DataBaseCommandPath.check();
      RestRequestHandler.NameLookup.setLookup(new NameLookup(e.player.getDisplayNameString(),e.player.getGameProfile().getId().toString()));
    }
  }

  @SubscribeEvent
  public void onRespawn(PlayerRespawnEvent e) {
    if (!vanishedPlayers.isEmpty() && vanishedPlayers.contains(e.player)) {
      GeneralUtils.updateVanish(e.player, false);
    }
    StoredPlayer playerData = PlayerUtils.get(e.player);
    if (playerData.server.homes.length > 0) {
      for (Home home : playerData.server.homes) {
        if (home.name.equalsIgnoreCase(GeneralModule.config.defaultHomeName)) {
          TeleportUtils.teleportTo(e.player, home);
          return;
        }
      }
    }
    if (e.player.getBedLocation() == null && getSpawn(playerData.global.rank) != null) {
      TeleportUtils.teleportTo(e.player, getSpawn(playerData.global.rank));
    }
  }

  private LocationWrapper getSpawn(String rank) {
    if (GeneralModule.config.spawn.spawns.get(rank) != null) {
      return GeneralModule.config.spawn.spawns.get(rank);
    } else if (GeneralModule.config.spawn.spawns.get("spawn") != null) {
      return GeneralModule.config.spawn.spawns.get("spawn");
    }
    return null;
  }

  public static void register(PlayerInventory inv) {
    openInv.put(inv.owner, inv);
  }

  public static void remove(PlayerInventory inv) {
    openInv.remove(inv.owner, inv);
  }

  public static void addFrozen(EntityPlayer player, BlockPos pos) {
    if (!frozenPlayers.keySet().contains(player)) {
      player.capabilities.disableDamage = true;
      frozenPlayers.put(player, pos);
      setFrozen(player.getGameProfile().getId(), true);
    }
  }

  public static void removeFrozen(EntityPlayer player) {
    if (frozenPlayers.size() > 0 && frozenPlayers.keySet().contains(player)) {
      frozenPlayers.remove(player);
      player.capabilities.disableDamage = false;
      setFrozen(player.getGameProfile().getId(), false);
    }
  }

  private static void setFrozen(UUID uuid, boolean frozen) {
    StoredPlayer playerData = (StoredPlayer) SECore.dataHandler
        .getData(DataKey.PLAYER, uuid.toString());
    playerData.server.frozen = frozen;
    SECore.dataHandler.registerData(DataKey.PLAYER, playerData);
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

  @SubscribeEvent
  public void tickStart(TickEvent.PlayerTickEvent e) {
    if (openInv.size() > 0 && openInv.containsKey(e.player)) {
      openInv.get(e.player).update();
    }
    if (frozenPlayers.size() > 0 && frozenPlayers.keySet().contains(e.player)) {
      BlockPos lockedPos = frozenPlayers.get(e.player);
      if (e.player.getPosition() != lockedPos) {
        e.player
            .setPositionAndUpdate(lockedPos.getX(), lockedPos.getY(), lockedPos.getZ());
      }
    }
  }

  @SubscribeEvent
  public void onDimChange(PlayerChangedDimensionEvent e) {
    if (!vanishedPlayers.isEmpty() && vanishedPlayers.contains(e.player)) {
      GeneralUtils.updateVanish(e.player, false);
    }
  }
}
