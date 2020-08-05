package com.wurmcraft.bot.json.api.json.rank;


import com.wurmcraft.bot.json.api.StoredDataType;

public class AutoRank implements StoredDataType {

  public int playTime;
  public long balance;
  public int exp;
  public String rank;
  public String nextRank;

  public AutoRank() {}

  public AutoRank(int playTime, long balance, int exp, String rank, String nextRank) {
    this.playTime = playTime;
    this.balance = balance;
    this.exp = exp;
    this.rank = rank;
    this.nextRank = nextRank;
  }

  @Override
  public String getID() {
    return rank;
  }
}
