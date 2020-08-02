package com.wurmcraft.serveressentials.forge.modules.economy.event;

import com.wurmcraft.serveressentials.forge.api.event.NewPlayerEvent;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import com.wurmcraft.serveressentials.forge.modules.economy.EconomyModule;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EconomyEvents {

  @SubscribeEvent
  public void newPlayer(NewPlayerEvent e) {
    e.newData.global.wallet.currency = new Currency[]{
        EconomyModule.config.defaultCurrency};
  }

}
