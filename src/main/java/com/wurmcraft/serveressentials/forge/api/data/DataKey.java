package com.wurmcraft.serveressentials.forge.api.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank;
import com.wurmcraft.serveressentials.forge.api.json.basic.Channel;
import com.wurmcraft.serveressentials.forge.api.json.basic.CurrencyConversion;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.api.json.rest.GlobalBan;

public enum DataKey {
  PLAYER("Player-Data", StoredPlayer.class, true),
  MODULE_CONFIG("Modules", null, false),
  LANGUAGE("Language", null, true),
  RANK("Rank", Rank.class, true),
  CURRENCY("Economy", CurrencyConversion.class, true),
  WARP("Warp", null, true),
  CHUNK_LOADING("ChunkLoading", null,true),
  BAN("Ban", GlobalBan.class, true),
  AUTO_RANK("AutoRank", AutoRank.class, true),
  CHANNEL("Channel", Channel.class, true);

  private String name;
  private Class<? extends JsonParser> dataType;
  private boolean inStorage;

  DataKey(String name,
      Class<? extends JsonParser> dataType, boolean inStorage) {
    this.name = name;
    this.dataType = dataType;
    this.inStorage = inStorage;
  }

  public String getName() {
    return name;
  }

  public Class<? extends JsonParser> getDataType() {
    return dataType;
  }

  public boolean isInStorage() {
    return inStorage;
  }
}