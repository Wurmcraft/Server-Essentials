package com.wurmcraft.serveressentials.common.modules.security;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Security")
public class ConfigSecurity {

  public boolean preventBlacklistedMods;
  public String[] modBlacklist;
  public String trustedList;
  public boolean lockdownEnabled;
  public boolean autoOP;
  public boolean checkAlt;

  public ConfigSecurity(boolean preventBlacklistedMods, String[] modBlacklist,
      String trustedList, boolean lockdownEnabled, boolean autoOP, boolean checkAlt) {
    this.preventBlacklistedMods = preventBlacklistedMods;
    this.modBlacklist = modBlacklist;
    this.trustedList = trustedList;
    this.lockdownEnabled = lockdownEnabled;
    this.autoOP = autoOP;
    this.checkAlt = checkAlt;
  }

  public ConfigSecurity() {
    this.preventBlacklistedMods = false;
    this.modBlacklist = new String[0];
    this.trustedList = "";
    this.lockdownEnabled = false;
    this.autoOP = true;
    this.checkAlt = false;
  }
}
