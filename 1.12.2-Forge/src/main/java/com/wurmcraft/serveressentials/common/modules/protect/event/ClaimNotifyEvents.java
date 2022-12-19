package com.wurmcraft.serveressentials.common.modules.protect.event;

import com.wurmcraft.serveressentials.ServerEssentials;
import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.Language;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.protect.ConfigProtect;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class ClaimNotifyEvents {

  public static HashMap<UUID, BlockPos> locationCache = new HashMap<>();
  public static HashMap<UUID, Claim> lastClaim = new HashMap<>();

  static int counter = 0;

  @SubscribeEvent
  public void onPlayerTick(PlayerTickEvent e) {
    if (counter == 0) {
      updatePlayers();
      // Reset for next time
      counter = ((ConfigProtect) SECore.moduleConfigs.get(
          "PROTECT")).trackingUpdateTimeTicks;
    } else {
      counter--;
    }
  }

  private static void updatePlayers() {
    for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance()
        .getPlayerList()
        .getPlayers()) {
      Claim previousClaim = lastClaim.getOrDefault(player.getGameProfile().getId(), null);
      BlockPos lastPos = locationCache.getOrDefault(player.getGameProfile().getId(),
          null);
      // Check if player has moved
      Claim currentClaim = ProtectionHelper.getClaim(player.getPosition(),
          player.dimension);
      if (lastPos != null && !lastPos.equals(player.getPosition())) {
        if (currentClaim != null && previousClaim != null && !currentClaim.owner.equals(
            previousClaim.owner)) {
          locationCache.put(player.getGameProfile().getId(), player.getPosition());
          lastClaim.put(player.getGameProfile().getId(), currentClaim);
          notifyClaimChange(player, currentClaim, true);
        } else if (previousClaim == null && currentClaim != null) {
          locationCache.put(player.getGameProfile().getId(), player.getPosition());
          lastClaim.put(player.getGameProfile().getId(), currentClaim);
          notifyClaimChange(player, currentClaim, true);
        } else if (currentClaim == null && previousClaim != null) {
          locationCache.put(player.getGameProfile().getId(), player.getPosition());
          lastClaim.put(player.getGameProfile().getId(), null);
          notifyClaimChange(player, null, false);
        }
      } else {
        lastClaim.put(player.getGameProfile().getId(), null);
        locationCache.put(player.getGameProfile().getId(), player.getPosition());
      }
    }
  }

  private static void notifyClaimChange(EntityPlayer player, Claim claim, boolean entry) {
    Language lang = SECore.dataLoader.get(DataType.LANGUAGE,
        SECore.dataLoader.get(DataType.ACCOUNT,
            player.getGameProfile().getId().toString(), new Account()).lang,
        new Language());
    String name = "Error";
    if (claim != null) {
      name = UsernameCache.getLastKnownUsername(UUID.fromString(claim.owner));
      if (name == null) {
        ServerEssentials.LOG.warn("Claim has invalid / unknown uuid as its owner!");
        ServerEssentials.LOG.warn(ServerEssentials.GSON.toJson(claim));
        name = "Error";
      }
    }
    if (entry) {
      ChatHelper.send(player, lang.PROTECT_CLAIM_ENTRY.replaceAll("\\{@NAME@}", name));
    } else {
      ChatHelper.send(player, lang.PROTECT_CLAIM_EXIT.replaceAll("\\{@NAME@}", name));
    }
  }

  // Unload player data upon leaving the server
  @SubscribeEvent(priority = EventPriority.LOW)
  public void onPlayerLeave(PlayerLoggedOutEvent e) {
    locationCache.remove(e.player.getGameProfile().getId());
    lastClaim.remove(e.player.getGameProfile().getId());
  }
}
