package com.wurmcraft.bot.json.api.eco;

import com.wurmcraft.bot.json.api.json.JsonParser;

public class Coin implements JsonParser {

  public String name;
  public double amount;

  public Coin(String name, double amount) {
    this.name = name;
    this.amount = amount;
  }
}
