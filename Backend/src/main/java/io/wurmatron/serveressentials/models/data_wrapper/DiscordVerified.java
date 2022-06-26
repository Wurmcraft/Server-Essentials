package io.wurmatron.serveressentials.models.data_wrapper;

public class DiscordVerified {

  public String discordID;
  public String discordName;
  public String uuid;
  public String username;

  public DiscordVerified(String discordID, String discordName, String uuid,
      String username) {
    this.discordID = discordID;
    this.discordName = discordName;
    this.uuid = uuid;
    this.username = username;
  }
}
