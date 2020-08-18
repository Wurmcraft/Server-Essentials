package com.wurmcraft.serveressentials.forge.modules.security;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Security")
public class SecurityConfig implements JsonParser {

  public String trustedUsers;
  public boolean autoOP;
  public boolean checkAlt;
  public String[] modBlacklist;

  public SecurityConfig() {
    this.trustedUsers = "";
    this.autoOP = true;
    this.checkAlt = false;
    this.modBlacklist = new String[0];
  }

  public SecurityConfig(String trustedUsers, boolean autoOP, boolean checkAlt,
      String[] modBlacklist) {
    this.trustedUsers = trustedUsers;
    this.autoOP = autoOP;
    this.checkAlt = checkAlt;
    this.modBlacklist = modBlacklist;
  }

  @Override
  public String getID() {
    return "Security";
  }
}
