package com.wurmcraft.serveressentials.common.modules.core;

import com.wurmcraft.serveressentials.api.loading.ModuleConfig;

@ModuleConfig(module = "Core")
public class ConfigCore {

  public String defaultLang;
  public String langStorageURL;

  public boolean iUseAModThatMessesWithNamesPleaseFix;

  public ConfigCore(
      String defaultLang, String langStorageURL, boolean iUseAModThatMessesWithNamesPleaseFix) {
    this.defaultLang = defaultLang;
    this.langStorageURL = langStorageURL;
    this.iUseAModThatMessesWithNamesPleaseFix = iUseAModThatMessesWithNamesPleaseFix;
  }

  public ConfigCore() {
    this.defaultLang = "en_us";
    this.langStorageURL =
        "https://raw.githubusercontent.com/Wurmcraft/Server-Essentials/dev/1.12.2-Forge/language";
    iUseAModThatMessesWithNamesPleaseFix = false;
  }
}
