package com.wurmcraft.serveressentials.api.models.data_wrapper;

public class PerkCost {

  public double costMultiplier;
  public double basicCost;
  public String perkNode;
  public int maxAmount;

  public PerkCost(double costMultiplier, double basicCost, String perkNode,
      int maxAmount) {
    this.costMultiplier = costMultiplier;
    this.basicCost = basicCost;
    this.perkNode = perkNode;
    this.maxAmount = maxAmount;
  }
}
