package com.wurmcraft.serveressentials.forge.api.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.json.basic.AutoRank;
import com.wurmcraft.serveressentials.forge.api.json.basic.CurrencyConversion;
import com.wurmcraft.serveressentials.forge.api.json.basic.Rank;
import com.wurmcraft.serveressentials.forge.api.json.player.StoredPlayer;
import com.wurmcraft.serveressentials.forge.api.json.rest.GlobalBan;

public enum DataKey {
  PLAYER("Player-Data", StoredPlayer.class),
  MODULE_CONFIG("Modules", null),
  LANGUAGE("Language", null),
  RANK("Rank", Rank.class),
  TPA("TPA", null),
  CURRENCY("Economy", CurrencyConversion.class),
  WARP("Warp", null),
  CHUNK_LOADING("ChunkLoading", null),
  BAN("Ban", GlobalBan.class),
  AUTO_RANK("AutoRank", AutoRank.class);

  private String name;
  private Class<? extends JsonParser> dataType;

  DataKey(String name,
      Class<? extends JsonParser> dataType) {
    this.name = name;
    this.dataType = dataType;
  }

  public String getName() {
    return name;
  }

  public Class<? extends JsonParser> getDataType() {
    return dataType;
  }
}