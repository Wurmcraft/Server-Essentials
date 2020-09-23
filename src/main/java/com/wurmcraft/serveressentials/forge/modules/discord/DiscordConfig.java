package com.wurmcraft.serveressentials.forge.modules.discord;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Discord")
public class DiscordConfig implements JsonParser {

  public String[] commandUponVerify;
  public String playerLoginNotificationChannel;


  public DiscordConfig() {
    this.commandUponVerify = new String[] {"eco %PLAYER% add 100"};
    playerLoginNotificationChannel = "global";
  }

  public DiscordConfig(String[] commandUponVerify,
      String playerLoginOutNotificationChannel) {
    this.commandUponVerify = commandUponVerify;
    this.playerLoginNotificationChannel = playerLoginOutNotificationChannel;
  }

  @Override
  public String getID() {
    return "Discord";
  }
}
