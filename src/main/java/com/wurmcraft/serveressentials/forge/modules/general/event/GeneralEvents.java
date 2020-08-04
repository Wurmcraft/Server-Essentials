package com.wurmcraft.serveressentials.forge.modules.general.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.general.GeneralModule;
import com.wurmcraft.serveressentials.forge.modules.general.command.teleport.SetHomeCommand;
import com.wurmcraft.serveressentials.forge.modules.general.utils.PlayerInventory;
import com.wurmcraft.serveressentials.forge.modules.rank.utils.RankUtils;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
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
    if (e.newData != null && SECore.config.spawn != null && !sentToSpawn.contains(e.newData.uuid)) {
      for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getPlayerList().getPlayers()) {
        if (player.getGameProfile().getId().toString().equals(e.newData.uuid)) {
          sentToSpawn.add(player.getGameProfile().getId().toString());
          TeleportUtils.teleportTo(player, SECore.config.spawn);
          return;
        }
      }
    }
  }

  @SubscribeEvent
  public void onPlayerJoin(PlayerLoggedInEvent e) {
    if (GeneralModule.config.displayMOTDOnJoin) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getCommandManager()
          .executeCommand(e.player, "/motd");
    }
  }

  @SubscribeEvent
  public void onRespawn(PlayerRespawnEvent e) {
//    if (!vanishedPlayers.isEmpty() && vanishedPlayers.contains(e.player)) {
//      VanishCommand.updatePlayer(e.player, false);
//    }
//    if (UserManager.getHome(e.player, SetHomeCommand.DEFAULT_HOME) != null) {
//      try {
//        TeleportUtils.teleportTo(
//            (EntityPlayerMP) e.player,
//            Objects.requireNonNull(UserManager.getHome(e.player, SetHomeCommand.DEFAULT_HOME))
//                .getPos(),
//            false,
//            false);
//      } catch (NullPointerException f) {
//        TeleportUtils.teleportTo(
//            (EntityPlayerMP) e.player, SECore.config.spawn);
//      }
//    } else if (SECore.config.spawn != null) {
//      TeleportUtils.teleportTo((EntityPlayerMP) e.player, SECore.config.spawn);
//    }
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
//    if (ServerEssentialsAPI.storageType.equalsIgnoreCase("File")) {
//      FileUser data = (FileUser) UserManager.getUserData(uuid)[0];
//      data.setFrozen(frozen);
//      DataHelper.save(Storage.USER, data);
//    } else if (ServerEssentialsAPI.storageType.equalsIgnoreCase("Rest")) {
//      LocalRestUser local = (LocalRestUser) UserManager.getUserData(uuid)[1];
//      local.setFrozen(frozen);
//      DataHelper.save(Storage.LOCAL_USER, local);
//    }
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
        e.player.setPositionAndUpdate(lockedPos.getX(), lockedPos.getY(), lockedPos.getZ());
      }
    }
  }

  @SubscribeEvent
  public void onDimChange(PlayerChangedDimensionEvent e) {
//    if (!vanishedPlayers.isEmpty() && vanishedPlayers.contains(e.player)) {
//      VanishCommand.updatePlayer(e.player, false);
//    }
  }
}
