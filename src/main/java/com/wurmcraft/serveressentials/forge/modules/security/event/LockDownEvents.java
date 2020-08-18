package com.wurmcraft.serveressentials.forge.modules.security.event;

import com.wurmcraft.serveressentials.forge.modules.security.command.LockDownCommand;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LockDownEvents {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockEvent.BreakEvent e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlace(BlockEvent.PlaceEvent e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onPlace(BlockEvent.FarmlandTrampleEvent e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickEmpty e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onRightClick(PlayerInteractEvent.RightClickItem e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onLeftClick(PlayerInteractEvent.LeftClickBlock e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onLeftClick(PlayerInteractEvent.LeftClickEmpty e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void explosion(ExplosionEvent e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void preventDeath(LivingHurtEvent e) {
    if (LockDownCommand.lockdown) {
      e.setCanceled(true);
    }
  }
}
