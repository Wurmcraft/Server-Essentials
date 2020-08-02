package com.wurmcraft.serveressentials.forge.modules.language;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Language")
public class LanguageConfig implements JsonParser {

  public int spamLimit;
  public String chatFormat;

  public LanguageConfig() {
    this.spamLimit = 3;
    this.chatFormat = "%PREFIX% %NAME% \u00BB%SUFFIX% %MESSAGE%";
  }

  public LanguageConfig(int spamLimit, String chatFormat) {
    this.spamLimit = spamLimit;
    this.chatFormat = chatFormat;
  }

  @Override
  public String getID() {
    return "Language";
  }
}
