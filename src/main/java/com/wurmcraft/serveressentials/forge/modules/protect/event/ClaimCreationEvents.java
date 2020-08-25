package com.wurmcraft.serveressentials.forge.modules.protect.event;

import com.wurmcraft.serveressentials.forge.api.SECore;
import com.wurmcraft.serveressentials.forge.api.data.DataKey;
import com.wurmcraft.serveressentials.forge.modules.protect.ProtectModule;
import com.wurmcraft.serveressentials.forge.modules.protect.data.ClaimData;
import com.wurmcraft.serveressentials.forge.modules.protect.data.ClaimData.Settings;
import com.wurmcraft.serveressentials.forge.modules.protect.data.RegionClaimData;
import com.wurmcraft.serveressentials.forge.modules.protect.data.RegionClaimData.RegionPos;
import com.wurmcraft.serveressentials.forge.modules.protect.utils.RegionHelper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import java.util.NoSuchElementException;
import java.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class ClaimCreationEvents {

  public static final ItemStack CLAIM_ITEM = new ItemStack(Items.GOLDEN_SHOVEL);
  public static NonBlockingHashMap<UUID, BlockPos> previousPos = new NonBlockingHashMap<>();

  @SubscribeEvent
  public void claimStart(PlayerInteractEvent.LeftClickBlock e) {
    if (e.getEntityPlayer().getHeldItemMainhand()
        .isItemEqualIgnoreDurability(CLAIM_ITEM)) {
      if (previousPos.containsKey(e.getEntityPlayer().getGameProfile().getId())) {
        if (getClaimSize(previousPos.get(e.getEntityPlayer().getGameProfile().getId()),
            e.getPos()) >= ProtectModule.config.minimumClaimAmount) {
          BlockPos lowerPos = null;
          BlockPos higherPos = null;
          if (isHigher(e.getPos(),
              previousPos.get(e.getEntityPlayer().getGameProfile().getId()))) {
            higherPos = e.getPos();
            lowerPos = previousPos.get(e.getEntityPlayer().getGameProfile().getId());
          } else {
            lowerPos = e.getPos();
            higherPos = previousPos.get(e.getEntityPlayer().getGameProfile().getId());
          }
          previousPos.remove(e.getEntityPlayer().getGameProfile().getId());
          ClaimData data = new ClaimData(
              e.getEntityPlayer().getGameProfile().getId().toString(), new String[0],
              lowerPos, higherPos, new Settings());
          String regionIDA = RegionHelper
              .getIDForRegion(lowerPos, e.getEntityPlayer().dimension);
          String regionIDB = RegionHelper
              .getIDForRegion(higherPos, e.getEntityPlayer().dimension);
          if (regionIDA.equals(regionIDB)) {
            try {
              RegionClaimData claimData = (RegionClaimData) SECore.dataHandler
                  .getData(DataKey.CLAIM, regionIDA);
              List<ClaimData> regionData = new ArrayList<>();
              Collections.addAll(regionData,claimData.claims);
              regionData.add(data);
              claimData.claims = regionData.toArray(new ClaimData[0]);
              SECore.dataHandler.registerData(DataKey.CLAIM, claimData);
            } catch (NoSuchElementException f) {
              RegionClaimData claimData = new RegionClaimData(
                  RegionHelper.getRegionPos(lowerPos, e.getEntityPlayer().dimension),
                  new ClaimData[]{data});
              SECore.dataHandler.registerData(DataKey.CLAIM, claimData);
            }
            ChatHelper.sendMessage(e.getEntityPlayer(), PlayerUtils.getLanguage(e.getEntityPlayer()).PROTECT_CREATED);
          } else {
            ChatHelper.sendMessage(e.getEntityPlayer(),
                "Multi Region Claims Currently Unsupported!");
          }
        } else {
          ChatHelper.sendMessage(e.getEntityPlayer(),
              PlayerUtils.getLanguage(e.getEntityPlayer()).PROTECT_TOO_SMALL
                  .replaceAll("%AMOUNT%", "" + ProtectModule.config.minimumClaimAmount));
        }
      } else {
        previousPos.put(e.getEntityPlayer().getGameProfile().getId(), e.getPos());
        ChatHelper.sendMessage(e.getEntityPlayer(),
            PlayerUtils.getLanguage(e.getEntityPlayer()).PROTECT_START);
      }
    }
  }

  private static int getClaimSize(BlockPos pos1, BlockPos pos2) {
    if (ProtectModule.config.claimType.equalsIgnoreCase("2D")) {
      return Math.abs(pos2.getX() - pos1.getX()) * Math.abs(pos2.getZ() - pos1.getZ());
    } else if (ProtectModule.config.claimType.equalsIgnoreCase("3D")) {
      return Math.abs(pos2.getX() - pos1.getX()) * Math.abs(pos2.getZ() - pos1.getZ())
          * Math.abs(pos2.getY() - pos1.getY());
    }
    return 0;
  }

  private static boolean isHigher(BlockPos pos1, BlockPos pos2) {
    if (pos1.getX() > pos2.getX() && pos1.getZ() > pos2.getZ() && pos1.getY() > pos2
        .getY()) {
      return true;
    }
    return false;
  }

  @SubscribeEvent
  public void playerTrack(LivingUpdateEvent e) {
    if (e.getEntityLiving() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) e.getEntityLiving();
      if (previousPos.containsKey(player.getGameProfile().getId())) {
        if (!player.getHeldItemMainhand().isItemEqualIgnoreDurability(CLAIM_ITEM)) {
          previousPos.remove(player.getGameProfile().getId());
          ChatHelper.sendMessage(player, PlayerUtils.getLanguage(player).PROTECT_STOP);
        }
      }
    }
  }

}
