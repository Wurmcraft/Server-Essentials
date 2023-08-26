package com.wurmcraft.serveressentials.common.modules.general.event;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.models.Account;
import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import com.wurmcraft.serveressentials.common.data.loader.DataLoader.DataType;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class HomeSpawnEvent {

  @SubscribeEvent
  public void onRespawn(PlayerRespawnEvent e) {
    LocalAccount localAccount = SECore.dataLoader.get(
        DataType.LOCAL_ACCOUNT, e.player.getGameProfile().getId().toString(),
        new LocalAccount());
    Account account = SECore.dataLoader.get(DataType.ACCOUNT,
        e.player.getGameProfile().getId().toString(), new Account());
    if (localAccount.homes.length > 0) {
      for (Home home : localAccount.homes) {
        if (home.name.equalsIgnoreCase(
            ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).defaultHomeName)) {
          TeleportUtils.teleportTo((EntityPlayerMP) e.player, localAccount, home, false);
          return;
        }
      }
    }
    if (e.player.getBedLocation() == null && PlayerUtils.getSpawn(account.rank) != null) {
      TeleportUtils.teleportTo((EntityPlayerMP) e.player, localAccount,
          PlayerUtils.getSpawn(account.rank));
    }
  }

}
