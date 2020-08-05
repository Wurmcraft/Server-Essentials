package com.wurmcraft.bot.json.api;

import com.wurmcraft.bot.json.api.player.Wallet;

public class Player {

  public String uuid;
  public String rank;
  public String discordID;
  public Wallet wallet;

  public Player(String uuid, String rank, String discordID,
      Wallet wallet) {
    this.uuid = uuid;
    this.rank = rank;
    this.discordID = discordID;
    this.wallet = wallet;
  }
}