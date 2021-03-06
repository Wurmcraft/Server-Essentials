package com.wurmcraft.serveressentials.forge.api.json.player;


/**
 * Stored the currencies a player holds.
 */
public class Wallet {

  public Currency[] currency;

  @Deprecated
  public Wallet() {
    this.currency = new Currency[0];
  }

  public Wallet(Currency[] currency) {
    this.currency = currency;
  }

  public static class Currency {

    public String name;
    public double amount;

    @Deprecated
    public Currency() {
      this.name = "";
      this.amount = 0;
    }

    /**
     * @param name Name of the currency
     * @param amount Amount of the currency
     */
    public Currency(String name, double amount) {
      this.name = name;
      this.amount = amount;
    }
  }

}
