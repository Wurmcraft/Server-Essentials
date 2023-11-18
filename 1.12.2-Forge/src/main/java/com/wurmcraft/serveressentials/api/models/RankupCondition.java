package com.wurmcraft.serveressentials.api.models;

public class RankupCondition {

  public String type;
  public String check;
  public String value;

  public RankupCondition(String type, String check, String value) {
    this.type = type;
    this.check = check;
    this.value = value;
  }
}
