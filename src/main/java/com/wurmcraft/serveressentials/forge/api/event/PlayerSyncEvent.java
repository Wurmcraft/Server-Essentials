package com.wurmcraft.serveressentials.forge.api.event;

import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.economy.command.EcoCommand;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerSyncEvent extends Event {

  public StoredPlayer currentData;
  public GlobalPlayer otherData;
  public StoredPlayer loadedPlayer;

  public PlayerSyncEvent(
      StoredPlayer currentData,
      GlobalPlayer otherData) {
    this.currentData = currentData;
    this.otherData = otherData;
    this.loadedPlayer = null;
  }
}
