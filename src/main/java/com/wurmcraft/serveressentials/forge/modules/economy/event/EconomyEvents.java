package com.wurmcraft.serveressentials.forge.modules.economy.event;

import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EconomyEvents {

  @SubscribeEvent()
  public void newPlayer(NewPlayerEvent e) {
    try {
      if (e.newData.global.wallet == null) {
        e.newData.global.wallet = new Wallet(new Currency[]{
            EconomyModule.config.defaultCurrency});
      }
    } catch (Exception f) {
      f.printStackTrace();
    }
  }

}
