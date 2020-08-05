package com.wurmcraft.bot.json.api.player;


import com.wurmcraft.bot.json.api.json.JsonParser;
import com.wurmcraft.bot.json.api.track.NetworkTime;

public class GlobalPlayer implements JsonParser {

  public String uuid;

  // Core
  public long firstJoin;
  public long lastSeen;

  // Rank
  public String rank;
  public String[] extraPerms;
  public String[] perks;

  // Language
  public String language;
  public boolean muted;

  // Economy
  public Wallet wallet;

  // Track
  public NetworkTime playtime;

  // Discord
  public String discordID;

  // Vote
  public int rewardPoints;

  public GlobalPlayer() {}

  public GlobalPlayer(
      String uuid,
      long firstJoin,
      long lastSeen,
      String rank,
      String[] extraPerms,
      String[] perks,
      String language,
      boolean muted,
      Wallet wallet,
      NetworkTime playtime,
      String discordID,
      int rewardPoints) {
    this.uuid = uuid;
    this.firstJoin = firstJoin;
    this.lastSeen = lastSeen;
    this.rank = rank;
    this.extraPerms = extraPerms;
    this.perks = perks;
    this.language = language;
    this.muted = muted;
    this.wallet = wallet;
    this.playtime = playtime;
    this.discordID = discordID;
    this.rewardPoints = rewardPoints;
  }
}
