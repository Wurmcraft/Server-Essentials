package com.wurmcraft.serveressentials.fabric.api.data;

public enum DataKey {
  PLAYER("Player-Data"),
  MODULE_CONFIG("Modules"),
  LANGUAGE("Language"),
  RANK("Rank"),
  TPA("TPA"),
  CURRENCY("Economy"),
  WARP("Warp"),
  CHUNK_LOADING("ChunkLoading"),
  AUTO_RANK("AutoRank");

  private String name;

  DataKey(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}