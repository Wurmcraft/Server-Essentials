package com.wurmcraft.serveressentials.forge.server.json;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import io.netty.bootstrap.Bootstrap;
import net.minecraft.server.dedicated.DedicatedServer;

public class MessagesConfig implements JsonParser {

  public String shutdownMessage;
  public MOTDSettings motd;

  public MessagesConfig() {
    this.shutdownMessage = "&cServer has shutdown";
    this.motd = new MOTDSettings();
  }

  public MessagesConfig(String shutdownMessage,
      MOTDSettings motd) {
    this.shutdownMessage = shutdownMessage;
    this.motd = motd;
  }

  @Override
  public String getID() {
    return "Messages";
  }
}
