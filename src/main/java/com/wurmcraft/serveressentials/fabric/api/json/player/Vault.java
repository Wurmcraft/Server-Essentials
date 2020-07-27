package com.wurmcraft.serveressentials.fabric.api.json.player;

/**
 * Stores items in a file-based e-chest
 *
 * @see ServerPlayer
 */
public class Vault {

  public String name;
  public String[] items;

  @Deprecated
  public Vault() {
    this.name = "";
    this.items = new String[0];
  }

  public Vault(String name) {
    this.name = name;
    this.items = new String[0];
  }

  public Vault(String name, String[] items) {
    this.name = name;
    this.items = items;
  }
}
