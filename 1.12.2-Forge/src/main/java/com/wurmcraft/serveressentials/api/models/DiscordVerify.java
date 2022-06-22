package com.wurmcraft.serveressentials.api.models;

public class DiscordVerify {

  public String token;
  public String uuid;
  public String username;
  public String discordID;
  public String discordUsername;

  public DiscordVerify(String token, String uuid, String username, String discordID,
      String discordUsername) {
    this.token = token;
    this.uuid = uuid;
    this.username = username;
    this.discordID = discordID;
    this.discordUsername = discordUsername;
  }
}
