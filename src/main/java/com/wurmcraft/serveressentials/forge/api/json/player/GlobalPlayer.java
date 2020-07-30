package com.wurmcraft.serveressentials.forge.api.json.player;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.json.player.Wallet.Currency;
import java.time.Instant;

/**
 * The second part of the entire data stored related to the player
 *
 * @see ServerPlayer
 * @see StoredPlayer
 */
public class GlobalPlayer implements JsonParser {

  public String uuid; // Player UUID
  public long firstJoin; // Unit Timestamp
  public long lastSeen; // Unit Timestamp
  public String rank; // User's rank
  public String language; // Users language
  public boolean muted; // Can the user talk in chat
  public Wallet wallet; // Currency the user currently has
  public NetworkTime playtime; // Playtime across all servers
  public String[] perks;    // Perks the user has gained
  public String[] extraPerms; // Perms added to the user on-top of his/her rank
  public String discordID; // ID of the user on discord (If verified)
  public int rewardPoints; // Reward Points (kinda like a secondary currency)

  public GlobalPlayer(String uuid) {
    this.uuid = uuid;
    this.firstJoin = Instant.EPOCH.toEpochMilli();
    this.lastSeen = Instant.EPOCH.toEpochMilli();
    this.rank = "";
    this.language = "";
    this.muted = false;
    this.wallet = new Wallet(new Currency[0]);
    this.playtime = new NetworkTime();
    this.perks = new String[0];
    this.extraPerms = new String[0];
    this.discordID = "";
    this.rewardPoints = 0;
  }

  public GlobalPlayer(String uuid, long firstJoin, long lastSeen, String rank,
      String language, boolean muted,
      Wallet wallet,
      NetworkTime playtime, String[] perks, String[] extraPerms, String discordID,
      int rewardPoints) {
    this.uuid = uuid;
    this.firstJoin = firstJoin;
    this.lastSeen = lastSeen;
    this.rank = rank;
    this.language = language;
    this.muted = muted;
    this.wallet = wallet;
    this.playtime = playtime;
    this.perks = perks;
    this.extraPerms = extraPerms;
    this.discordID = discordID;
    this.rewardPoints = rewardPoints;
  }

  @Override
  public String getID() {
    return null;
  }
}
