package com.wurmcraft.serveressentials.api.models;

import java.util.Map;

public class Channel {

  public String name;
  public String prefix;
  public boolean logChat;
  public Map<String, String> chatReplacement;
  public boolean enabled;
  public String chatFormat;
  public boolean allowLinks;
  public String[] linkBlacklist;

  public Channel() {
  }

  public Channel(String name, String prefix, boolean logChat,
      Map<String, String> chatReplacement, boolean enabled, String chatFormat,
      boolean allowLinks, String[] linkBlacklist) {
    this.name = name;
    this.prefix = prefix;
    this.logChat = logChat;
    this.chatReplacement = chatReplacement;
    this.enabled = enabled;
    this.chatFormat = chatFormat;
    this.allowLinks = allowLinks;
    this.linkBlacklist = linkBlacklist;
  }
}
