package com.wurmcraft.serveressentials.fabric.api.json.player;


import com.wurmcraft.serveressentials.fabric.api.json.basic.LocationWrapper;
import java.time.Instant;
import java.util.HashMap;


/**
 * The first part of the entire data stored related to the player
 *
 * @see GlobalPlayer
 * @see StoredPlayer
 */
public class ServerPlayer {

  public long firstJoin; // Unit Timestamp
  public long lastSeen; // Unit Timestamp
  public Home[] homes;
  public Vault[] vaults;
  public LocationWrapper lastLocation;
  public long teleportTimer; // System MS
  public HashMap<String, Long> kitUsage; // Name, LastUsed (System MS)
  public String channel;
  public String nick;

  public ServerPlayer() {
    this.firstJoin = Instant.EPOCH.toEpochMilli();
    this.lastSeen = Instant.EPOCH.toEpochMilli();
    this.homes = new Home[0];
    this.vaults = new Vault[0];
    this.lastLocation = null;
    this.teleportTimer = 0;
    this.kitUsage = new HashMap<>();
    this.channel = "";
    this.nick = "";
  }

  public ServerPlayer(long firstJoin, long lastSeen,
      Home[] homes, Vault[] vaults,
      LocationWrapper lastLocation, long teleportTimer,
      HashMap<String, Long> kitUsage, String channel, String nick) {
    this.firstJoin = firstJoin;
    this.lastSeen = lastSeen;
    this.homes = homes;
    this.vaults = vaults;
    this.lastLocation = lastLocation;
    this.teleportTimer = teleportTimer;
    this.kitUsage = kitUsage;
    this.channel = channel;
    this.nick = nick;
  }
}
