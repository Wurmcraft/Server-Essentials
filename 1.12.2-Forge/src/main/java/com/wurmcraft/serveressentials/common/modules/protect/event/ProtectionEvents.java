package com.wurmcraft.serveressentials.common.modules.protect.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.protect.ConfigProtect;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.TrustInfo.Action;
import com.wurmcraft.serveressentials.common.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.lang.reflect.Field;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ProtectionEvents {

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onBlockBreak(BreakEvent e) {
    Claim claim = ProtectionHelper.getClaim(e.getPos(), e.getPlayer().dimension);
    if (claim != null) {
      if (!ProtectionHelper.isAllowed(claim, e.getPlayer(), Action.BREAK)) {
        e.setCanceled(true);
        Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
            SECore.dataLoader.get(DataType.ACCOUNT,
                e.getPlayer().getGameProfile().getId().toString(), new Account()).lang,
            new Language());
        ChatHelper.send(e.getPlayer(), lang.PROTECT_BREAK);
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onBlockPlace(EntityPlaceEvent e) {
    if (e.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntity();
      Claim claim = ProtectionHelper.getClaim(e.getPos(), player.dimension);
      if (claim != null) {
        if (!ProtectionHelper.isAllowed(claim, player, Action.PLACE)) {
          e.setCanceled(true);
          Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
              SECore.dataLoader.get(DataType.ACCOUNT,
                  player.getGameProfile().getId().toString(), new Account()).lang,
              new Language());
          ChatHelper.send(player, lang.PROTECT_PLACE);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onFarmlandTrample(FarmlandTrampleEvent e) {
    if (e.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntity();
      Claim claim = ProtectionHelper.getClaim(e.getPos(), player.dimension);
      if (claim != null) {
        if (!ProtectionHelper.isAllowed(claim, player, Action.PLACE)) {
          e.setCanceled(true);
          Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
              SECore.dataLoader.get(DataType.ACCOUNT,
                  player.getGameProfile().getId().toString(), new Account()).lang,
              new Language());
          ChatHelper.send(player, lang.PROTECT_PLACE);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onBlockPlace(RightClickBlock e) {
    if (e.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntity();
      Claim claim = ProtectionHelper.getClaim(e.getPos(), player.dimension);
      if (claim != null) {
        if (!ProtectionHelper.isAllowed(claim, player, Action.INTERACT)) {
          e.setCanceled(true);
          Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
              SECore.dataLoader.get(DataType.ACCOUNT,
                  player.getGameProfile().getId().toString(), new Account()).lang,
              new Language());
          ChatHelper.send(player, lang.PROTECT_PLACE);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onBlockPlace(LeftClickBlock e) {
    if (e.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntity();
      Claim claim = ProtectionHelper.getClaim(e.getPos(), player.dimension);
      if (claim != null) {
        if (!ProtectionHelper.isAllowed(claim, player, Action.INTERACT)) {
          e.setCanceled(true);
          Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
              SECore.dataLoader.get(DataType.ACCOUNT,
                  player.getGameProfile().getId().toString(), new Account()).lang,
              new Language());
          ChatHelper.send(player, lang.PROTECT_PLACE);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onBlockPlace(RightClickEmpty e) {
    if (e.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntity();
      Claim claim = ProtectionHelper.getClaim(e.getPos(), player.dimension);
      if (claim != null) {
        if (!ProtectionHelper.isAllowed(claim, player, Action.INTERACT)) {
          e.setCanceled(true);
          Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
              SECore.dataLoader.get(DataType.ACCOUNT,
                  player.getGameProfile().getId().toString(), new Account()).lang,
              new Language());
          ChatHelper.send(player, lang.PROTECT_PLACE);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onBlockPlace(RightClickItem e) {
    Claim claim = ProtectionHelper.getClaim(e.getPos(), e.getEntityPlayer().dimension);
    if (claim != null) {
      if (!ProtectionHelper.isAllowed(claim, e.getEntityPlayer(), Action.INTERACT)) {
        e.setCanceled(true);
      } else if (isInteractionAllowedArea(e.getPos(), e.getEntityPlayer())) {
        e.setCanceled(true);
      }
    }
  }

  private static int maxCheck = ((ConfigProtect) SECore.moduleConfigs.get(
      "PROTECT")).defenseRange;
  private static int minDistance = ((ConfigProtect) SECore.moduleConfigs.get(
      "PROTECT")).minClaimSize;
  private static boolean preventNearbyExplosions = ((ConfigProtect) SECore.moduleConfigs.get(
      "PROTECT")).preventNearbyExplosions;

  private boolean isInteractionAllowedArea(BlockPos pos, EntityPlayer player) {
    for (int x = -maxCheck; x < maxCheck; x = x + minDistance) {
      for (int z = -maxCheck; z < maxCheck; z = z + minDistance) {
        for (int y = -maxCheck; y < maxCheck; y = y + minDistance) {
          Claim claim = ProtectionHelper.getClaim(pos.add(x, y, z), player.dimension);
          if (!ProtectionHelper.isAllowed(claim, player, Action.INTERACT)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onBlockPlace(LeftClickEmpty e) {
    if (e.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntity();
      Claim claim = ProtectionHelper.getClaim(e.getPos(), player.dimension);
      if (claim != null) {
        if (!ProtectionHelper.isAllowed(claim, player, Action.INTERACT)) {
          e.setCanceled(true);
          Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
              SECore.dataLoader.get(DataType.ACCOUNT,
                  player.getGameProfile().getId().toString(), new Account()).lang,
              new Language());
          ChatHelper.send(player, lang.PROTECT_PLACE);
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void onExplosion(ExplosionEvent.Detonate e) {
    if (e.getExplosion().getExplosivePlacedBy() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getExplosion().getExplosivePlacedBy();
      if (!allowedToExplode(e.getExplosion().getAffectedBlockPositions(), player)) {
        e.setCanceled(true);
        ServerEssentials.LOG.info("Explosion has been canceled at " + e.getExplosion()
            .getAffectedBlockPositions().get(0).toString() + " by " + e.getExplosion()
            .getExplosivePlacedBy().getDisplayName().getFormattedText());
        Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
            SECore.dataLoader.get(DataType.ACCOUNT,
                player.getGameProfile().getId().toString(), new Account()).lang,
            new Language());
        ChatHelper.send(player, lang.PROTECT_PLACE);
      }
    } else {
      int dim = e.getExplosion().getExplosivePlacedBy() != null ? e.getExplosion()
          .getExplosivePlacedBy().dimension : e.getWorld().provider.getDimension();
      if (dim == Integer.MIN_VALUE) {
        ServerEssentials.LOG.warn(
            "Unable to determine explosion source, \"I won't interfere\" ("
                + e.getExplosion().getExplosivePlacedBy() + ")");
      } else if (hasClaim(e.getExplosion().getAffectedBlockPositions(), dim)) {
        if (preventNearbyExplosions) {
          e.setCanceled(true);
        } else {
          String fieldName = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") ? "affectedBlockPositions" : "field_149155_e";
          try {
            Field field = e.getExplosion().getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(e.getExplosion(),safeSpots(e.getExplosion().getAffectedBlockPositions(), dim));
          } catch (Exception f) {
            f.printStackTrace();
            ServerEssentials.LOG.info("Failed to override explosion field, preventing explosion to protect claim");
            e.setCanceled(true);
          }
        }
      }
    }
  }

  public static boolean allowedToExplode(List<BlockPos> explosionList,
      EntityPlayer player) {
    for (BlockPos pos : explosionList) {
      Claim claim = ProtectionHelper.getClaim(pos, player.dimension);
      if (!ProtectionHelper.isAllowed(claim, player, Action.BREAK)) {
        return false;
      }
    }
    return true;
  }

  public static boolean hasClaim(List<BlockPos> explosionList, int dim) {
    for (BlockPos pos : explosionList) {
      Claim claim = ProtectionHelper.getClaim(pos, dim);
      if (claim != null) {
        return true;
      }
    }
    return false;
  }

  public static List<BlockPos> safeSpots(List<BlockPos> possibleSpots, int dim) {
    List<BlockPos> safeSpots = new ArrayList<>();
    for (BlockPos pos : possibleSpots) {
      Claim claim = ProtectionHelper.getClaim(pos, dim);
      if (claim == null) {
        safeSpots.add(pos);
      }
    }
    return safeSpots;
  }
}
