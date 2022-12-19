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
    this.trustedList = "https://gist.githubusercontent.com/Wurmatron/f06ddc6ac25ef3b26d34ec4a8a2ba279/raw/59eb05aaa8d34c5907d1399adcf60f01266a6d32/trusted.txt";
    this.lockdownEnabled = false;
    this.autoOP = true;
    this.checkAlt = false;
  }
}
