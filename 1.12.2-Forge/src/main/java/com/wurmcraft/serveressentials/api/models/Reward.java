package com.wurmcraft.serveressentials.api.models;

public class Reward {

  public String item;
  public int chance;
  public int tier;

  /**
   * @param item item converted to string
   * @param chance chance higher number == higher chance
   * @param tier how many points required to even roll it
   */
  public Reward(String item, int chance, int tier) {
    this.item = item;
    this.chance = chance;
    this.tier = tier;
  }

  public Reward(String item, int chance) {
    this.item = item;
    this.chance = chance;
    this.tier = 0;
  }
}
