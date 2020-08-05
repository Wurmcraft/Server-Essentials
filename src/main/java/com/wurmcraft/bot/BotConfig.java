package com.wurmcraft.bot;

public class BotConfig {

  // Messages
  public String verifyCodeMessage;
  public String userVerified;


  // Rest
  public String discordBotToken;
  public String restURL;
  public String restAuth;

  // General
  public int updateTime;
  public String discordServerID;
  public long verifiedRole;
  public int codeLength;

  public BotConfig() {
    this.verifyCodeMessage = "In-game type /verifycode <code> on any of the network servers, to link your discord account with your minecraft account";
    this.userVerified = "You have been verified";
    this.discordBotToken = "bot-token";
    this.restURL = "https://rest.xxxx.com/api";
    this.restAuth = "username:password";
    this.updateTime = 5;
    this.discordServerID = "discord-server-id";
    this.verifiedRole = 0;
    this.codeLength = 12;
  }

  public BotConfig(String verifyMessage, String discordBotToken, String restURL,
      String restAuth, int updateTime, String discordServerID, long verifiedRole,
      int codeLength) {
    this.verifyCodeMessage = verifyMessage;
    this.discordBotToken = discordBotToken;
    this.restURL = restURL;
    this.restAuth = restAuth;
    this.updateTime = updateTime;
    this.discordServerID = discordServerID;
    this.verifiedRole = verifiedRole;
    this.codeLength = codeLength;
  }
}
