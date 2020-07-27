package com.wurmcraft.serveressentials.forge.api.json.basic;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

/**
 * Used to exchange one currency to another
 */
public class CurrencyConversion implements JsonParser {

  public String name;
  public double worth;

  public CurrencyConversion(String name, double worth) {
    this.name = name;
    this.worth = worth;
  }

  public CurrencyConversion(String name) {
    this.name = name;
    this.worth = 1;
  }

  @Override
  public String getID() {
    return name;
  }
}
