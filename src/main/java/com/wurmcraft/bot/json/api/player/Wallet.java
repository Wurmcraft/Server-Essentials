package com.wurmcraft.bot.json.api.player;


import com.wurmcraft.bot.json.api.eco.Coin;
import com.wurmcraft.bot.json.api.json.JsonParser;

public class Wallet implements JsonParser {

  public Coin[] currency;

  public Wallet(Coin[] currency) {
    this.currency = currency;
  }
}
