package com.wurmcraft.bot.json.api.player;


import com.wurmcraft.bot.json.api.json.JsonParser;

public class Vault implements JsonParser {

  public String items;

  public Vault(String items) {
    this.items = items;
  }
}
