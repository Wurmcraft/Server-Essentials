package com.wurmcraft.serveressentials.forge.api.json.basic;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class AutoRank implements JsonParser {

  public String rank;
  public String nextRank;
  public int playTime;
  public long balance;
  public int exp;

  public AutoRank() {
    this.rank = "Error";
    this.nextRank = "";
    this.playTime = -1;
    this.balance = 0;
    this.exp = 0;
  }

  public AutoRank(String rank, String nextRank, int playTime) {
    this.rank = rank;
    this.nextRank = nextRank;
    this.playTime = playTime;
    this.balance = 0;
    this.exp = 0;
  }

  public AutoRank(String rank, String nextRank, int playTime, long balance, int exp) {
    this.rank = rank;
    this.nextRank = nextRank;
    this.playTime = playTime;
    this.balance = balance;
    this.exp = exp;
  }

  @Override
  public String getID() {
    return rank;
  }
}
