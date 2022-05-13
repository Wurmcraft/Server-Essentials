package com.wurmcraft.serveressentials.common.modules.security;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Security")
public class ConfigSecurity {

  public boolean preventBlacklistedMods;
  public String[] modBlacklist;
  public String trustedList;
  public boolean lockdownEnabled;

  public ConfigSecurity(
      boolean preventBlacklistedMods,
      String[] modBlacklist,
      String trustedList,
      boolean lockdownEnabled) {
    this.preventBlacklistedMods = preventBlacklistedMods;
    this.modBlacklist = modBlacklist;
    this.trustedList = trustedList;
    this.lockdownEnabled = lockdownEnabled;
  }

  public ConfigSecurity() {
    this.preventBlacklistedMods = false;
    this.modBlacklist = new String[0];
    this.trustedList = "";
    this.lockdownEnabled = false;
  }
}
