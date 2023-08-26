package com.wurmcraft.serveressentials.common.modules.general.event;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.common.command.RankUtils;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import org.cliffc.high_scale_lib.NonBlockingHashSet;

public class VanishEvent {

  public static NonBlockingHashSet<EntityPlayer> vanishedPlayers = new NonBlockingHashSet<>();

  static int update = 0;

  @SubscribeEvent
  public void onUpdate(WorldTickEvent e) {
    if (update == 0) {
      PotionEffect effect = new PotionEffect(
          Potion.getPotionFromResourceLocation("invisibility"), 200, 2, true, false);
      for (EntityPlayer player : vanishedPlayers) {
        player.addPotionEffect(effect);
      }
      update = 160;
    } else {
      update--;
    }
  }

  @SubscribeEvent
  public void onRespawn(PlayerRespawnEvent e) {
    if (!vanishedPlayers.isEmpty() && vanishedPlayers.contains(e.player)) {
      updateVanish(e.player, false);
    }
  }

  @SubscribeEvent
  public void onDimChange(PlayerChangedDimensionEvent e) {
    if (!vanishedPlayers.isEmpty() && vanishedPlayers.contains(e.player)) {
      updateVanish(e.player, false);
    }
  }

  public static void updateVanish(EntityPlayer player, boolean track) {
    if (track) {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getWorld(player.dimension)
          .getEntityTracker()
          .track(player);
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getWorld(player.dimension)
          .getEntityTracker()
          .getTrackingPlayers(player)
          .forEach(
              tp -> {
                ((EntityPlayerMP) player).connection.sendPacket(
                    new SPacketSpawnPlayer(tp));
              });
    } else {
      FMLCommonHandler.instance()
          .getMinecraftServerInstance()
          .getWorld(player.dimension)
          .getEntityTracker()
          .untrack(player);
      for (EntityPlayer tp : FMLCommonHandler.instance().getMinecraftServerInstance()
          .getWorld(player.dimension).getEntityTracker().getTrackingPlayers(player)) {
        Account account = SECore.dataLoader.get(DataType.ACCOUNT,
            tp.getGameProfile().getId().toString(), new Account());
        if (!RankUtils.hasPermission(account, "command.vanish.see")) {
          ((EntityPlayerMP) player).connection.sendPacket(new SPacketSpawnPlayer(tp));
        }
      }
    }
  }
}
