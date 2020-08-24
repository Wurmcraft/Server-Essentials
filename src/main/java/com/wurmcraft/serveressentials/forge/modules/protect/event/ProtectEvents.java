package com.wurmcraft.serveressentials.forge.modules.protect.event;

import com.wurmcraft.serveressentials.forge.modules.protect.ProtectModule;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.ProtectionHelper.Action;
import java.util.*;
import java.util.Objects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.world.BlockEvent.FluidPlaceBlockEvent;
import net.minecraftforge.event.world.BlockEvent.PortalSpawnEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ProtectEvents {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockEvent.BreakEvent e) {
    if (!ProtectionHelper
        .test(Action.BREAK, e.getPos(), e.getPlayer().dimension, e.getPlayer())) {
      e.getPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockEvent.PlaceEvent e) {
    if (!ProtectionHelper
        .test(Action.PLACE, e.getPos(), e.getPlayer().dimension, e.getPlayer())) {
      e.getPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
    if (!ProtectionHelper
        .test(Action.PLACE, e.getPos(),
            e.getBlockSnapshot().getWorld().provider.getDimension(), null)) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void changeMineSpeed(BreakSpeed e) {
    if (!ProtectionHelper
        .test(Action.BREAK, e.getPos(), e.getEntityPlayer().dimension,
            e.getEntityPlayer())) {
      e.setNewSpeed(0);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void changeMineSpeed(FluidPlaceBlockEvent e) {
    if (!ProtectionHelper
        .test(Action.BREAK, e.getPos(), e.getWorld().provider.getDimension(),
            null)) {
      e.setNewState(Blocks.STONE.getDefaultState());
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void changeMineSpeed(PortalSpawnEvent e) {
    if (!ProtectionHelper
        .test(Action.BREAK, e.getPos(), e.getWorld().provider.getDimension(),
            null)) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockEvent.MultiPlaceEvent e) {
    if (!ProtectionHelper
        .test(Action.PLACE, e.getPos(), e.getPlayer().dimension, e.getPlayer())) {
      e.getPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent e) {
    if (!ProtectionHelper.test(Action.BREAK, e.getPos(), e.getEntity().dimension, null)) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onExplosion(ExplosionEvent e) {
    EntityPlayer player = null;
    if (e.getExplosion().getExplosivePlacedBy() instanceof EntityPlayer) {
      player = (EntityPlayer) e.getExplosion().getExplosivePlacedBy();
    }
    if (!ProtectionHelper.test(Action.EXPLOSION,
        new BlockPos(e.getExplosion().getPosition().x, e.getExplosion().getPosition().y,
            e.getExplosion().getPosition().z), e.getWorld().provider.getDimension(),
        player)) {
      if (player != null) {
        player.sendMessage(new TextComponentString("Area is Claimed!"));
      }
      e.setCanceled(true);
      return;
    }
    for (int x = (int) (e.getExplosion().getPosition().x
        - ProtectModule.config.explosionExpandedRadius); x
        < e.getExplosion().getPosition().x + ProtectModule.config.explosionExpandedRadius;
        x = x + ProtectModule.config.explosionCheckResolution) {
      for (int z = (int) (e.getExplosion().getPosition().z
          - ProtectModule.config.explosionExpandedRadius); z
          < e.getExplosion().getPosition().z
          + ProtectModule.config.explosionExpandedRadius;
          z = z + ProtectModule.config.explosionCheckResolution) {
        for (int y = (int) (e.getExplosion().getPosition().y
            - ProtectModule.config.explosionExpandedRadius); y
            < e.getExplosion().getPosition().y
            + ProtectModule.config.explosionExpandedRadius;
            y = y + ProtectModule.config.explosionCheckResolution) {
          if (!ProtectionHelper.test(Action.EXPLOSION, new BlockPos(x, y, z),
              e.getWorld().provider.getDimension(), player)) {
            if (player != null) {
              player.sendMessage(new TextComponentString("Area is Claimed!"));
            }
            e.setCanceled(true);
            return;
          }
        }
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onLeftClick(PlayerInteractEvent.LeftClickBlock e) {
    if (!ProtectionHelper
        .test(Action.LEFT_CLICK, e.getPos(), e.getEntityPlayer().dimension,
            e.getEntityPlayer())) {
      e.getEntityPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onLeftClick(PlayerInteractEvent.LeftClickEmpty e) {
    if (!ProtectionHelper
        .test(Action.LEFT_CLICK_EMPTY, e.getPos(), e.getEntityPlayer().dimension,
            e.getEntityPlayer())) {
      e.getEntityPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickItem e) {
    if (!ProtectionHelper
        .test(Action.RIGHT_CLICK, e.getPos(), e.getEntityPlayer().dimension,
            e.getEntityPlayer())) {
      e.getEntityPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
    if (!ProtectionHelper
        .test(Action.RIGHT_CLICK, e.getPos(), e.getEntityPlayer().dimension,
            e.getEntityPlayer())) {
      e.getEntityPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickEmpty e) {
    if (!ProtectionHelper
        .test(Action.RIGHT_CLICK_EMPTY, e.getPos(), e.getEntityPlayer().dimension,
            e.getEntityPlayer())) {
      e.getEntityPlayer().sendMessage(new TextComponentString("Area is Claimed!"));
      e.setCanceled(true);
    }
  }
}
