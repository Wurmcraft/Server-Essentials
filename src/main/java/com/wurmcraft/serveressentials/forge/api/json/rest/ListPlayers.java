package com.wurmcraft.serveressentials.forge.api.json.rest;

import com.wurmcraft.serveressentials.forge.api.json.player.Wallet;

/**
 * Allows the server to query all users without too much extra data
 */
public class ListPlayers {

  public PlayerSimple[] players;

  public ListPlayers(
      PlayerSimple[] players) {
    this.players = players;
  }

  public static class PlayerSimple {

    public String uuid;
    public String rank;
    public String discordID;
    public Wallet wallet;

    public PlayerSimple(String uuid, String rank, String discordID,
        Wallet wallet) {
      this.uuid = uuid;
      this.rank = rank;
      this.discordID = discordID;
      this.wallet = wallet;
    }
  }

}
