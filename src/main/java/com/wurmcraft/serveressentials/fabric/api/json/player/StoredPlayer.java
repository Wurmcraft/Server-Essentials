package com.wurmcraft.serveressentials.fabric.api.json.player;

import com.wurmcraft.serveressentials.fabric.api.json.JsonParser;

/**
 * Stored all the data related to the player
 *
 * @see GlobalPlayer
 * @see ServerPlayer
 */
public class StoredPlayer implements JsonParser {

  public String uuid;
  public ServerPlayer server;
  public GlobalPlayer global;

  public StoredPlayer(String uuid) {
    this.uuid = uuid;
    this.server = new ServerPlayer();
    this.global = new GlobalPlayer(uuid);
  }

  public StoredPlayer(String uuid,
      ServerPlayer server,
      GlobalPlayer global) {
    this.uuid = uuid;
    this.server = server;
    this.global = global;
  }

  @Override
  public String getID() {
    return uuid;
  }
}
