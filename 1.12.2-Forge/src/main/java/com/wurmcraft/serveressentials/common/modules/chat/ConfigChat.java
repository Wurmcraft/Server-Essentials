package com.wurmcraft.serveressentials.common.modules.chat;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Chat")
public class ConfigChat {

  public String defaultChannel;
  public String defaultChatFormat;
  public String nickFormat;
  public String messageFormat;
  public String defaultMuteDuration;
  public String[] motd;
  public boolean displayUUIDOnHover;

  public ConfigChat(String defaultChannel, String defaultChatFormat, String nickFormat, String messageFormat, String defaultMuteDuration, String[] motd, boolean displayUUIDOnHover) {
    this.defaultChannel = defaultChannel;
    this.defaultChatFormat = defaultChatFormat;
    this.nickFormat = nickFormat;
    this.messageFormat = messageFormat;
    this.defaultMuteDuration = defaultMuteDuration;
    this.motd = motd;
    this.displayUUIDOnHover = displayUUIDOnHover;
  }
  public ConfigChat() {
    this.defaultChannel = "local";
    this.defaultChatFormat = "%CHANNEL_PREFIX% %RANK_PREFIX% %NAME% %RANK_SUFFIX%: %MESSAGE%";
    this.defaultMuteDuration = "10m";
    this.nickFormat = "*%NICK%";
    this.messageFormat = "%NAME% {->, <-} %MSG%";
    this.motd =
        new String[] {"&bWelcome %NAME%,", "&bThis server is running with Server Essentials"};
    this.displayUUIDOnHover = false;
  }
}
