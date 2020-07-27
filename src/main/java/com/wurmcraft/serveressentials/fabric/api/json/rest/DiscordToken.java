package com.wurmcraft.serveressentials.fabric.api.json.rest;

/**
 * Used by SE bot to verify a user's uuid <-> discordID
 */
public class DiscordToken {

  public String id;
  public String token;

  public DiscordToken(String id, String token) {
    this.id = id;
    this.token = token;
  }
}
