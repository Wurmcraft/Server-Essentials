package com.wurmcraft.serveressentials.forge.api.event;

import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class NewPlayerEvent extends Event {

  public StoredPlayer newData;

  public NewPlayerEvent(
      StoredPlayer newData) {
    this.newData = newData;
  }
}
