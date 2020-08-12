package com.wurmcraft.serveressentials.forge.modules.security;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Security")
public class SecurityConfig implements JsonParser {

  public String trustedUsers;

  public SecurityConfig() {
    this.trustedUsers = "";
  }

  public SecurityConfig(String trustedUsers) {
    this.trustedUsers = trustedUsers;
  }

  @Override
  public String getID() {
    return "Security";
  }
}
