package com.wurmcraft.serveressentials.forge.modules.chatbridge.json;

public class BridgeMessage {

  public String message;
  public String id;
  public String userID;
  public String displayName;
  public String channel;
  public String discordChannelID;
  public int formattingStyle;

  public BridgeMessage(String message, String id, String userID,
      String displayName, String channel, String discordChannelID, int formattingStyle) {
    this.message = message;
    this.id = id;
    this.userID = userID;
    this.displayName = displayName;
    this.channel = channel;
    this.discordChannelID = discordChannelID;
    this.formattingStyle = formattingStyle;
  }
}
