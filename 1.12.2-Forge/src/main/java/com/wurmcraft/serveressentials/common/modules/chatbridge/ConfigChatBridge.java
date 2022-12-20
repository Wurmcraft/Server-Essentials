package com.wurmcraft.serveressentials.common.modules.chatbridge;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;
import java.util.HashMap;
import java.util.Map;

@ModuleConfig(module = "ChatBridge")
public class ConfigChatBridge {

  public Discord discord;
  public Redirect redirect;

  public ConfigChatBridge(Discord discord, Redirect redirect) {
    this.discord = discord;
    this.redirect = redirect;
  }

  public ConfigChatBridge() {
    this.discord = new Discord();
    this.redirect = new Redirect();
  }

  public static class Discord {

    public boolean enabled;
    public Map<String, String> channelMapping;

    public Discord(boolean enabled, Map<String, String> channelMapping) {
      this.enabled = enabled;
      this.channelMapping = channelMapping;
    }

    public Discord() {
      this.enabled = false;
      this.channelMapping = new HashMap<>();
    }
  }

  public static class Redirect {

    public Map<String, String> channelRemap;

    public Redirect(Map<String, String> channelRemap) {
      this.channelRemap = channelRemap;
    }

    public Redirect() {
      this.channelRemap = new HashMap<>();
    }
  }
}
