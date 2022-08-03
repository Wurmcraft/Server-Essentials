package com.wurmcraft.serveressentials.common.modules.discord;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Discord")
public class ConfigDiscord {

  public String[] verifyCommands;

  public ConfigDiscord(String[] verifyCommands) {
    this.verifyCommands = verifyCommands;
  }

  public ConfigDiscord() {
    this.verifyCommands = new String[]{"eco add {username} 1000"};
  }
}
