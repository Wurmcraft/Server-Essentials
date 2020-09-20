package com.wurmcraft.serveressentials.forge.api.json.player;

public class NameLookup {

  public String username;
  public String uuid;

  public NameLookup(String username, String uuid) {
    this.username = username;
    this.uuid = uuid;
  }
}
