package com.wurmcraft.serveressentials.forge.api.event;

import com.wurmcraft.serveressentials.forge.api.json.player.GlobalPlayer;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.modules.economy.command.EcoCommand;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerSyncEvent extends Event {

  public String uuid;
  public StoredPlayer currentData;
  public GlobalPlayer otherData;
  public StoredPlayer loadedPlayer;

  public PlayerSyncEvent(String uuid,
      StoredPlayer currentData,
      GlobalPlayer otherData) {
    this.uuid = uuid;
    this.currentData = currentData;
    this.otherData = otherData;
    this.loadedPlayer = null;
  }
}
