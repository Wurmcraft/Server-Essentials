package com.wurmcraft.serveressentials.forge.modules.language;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Language")
public class LanguageConfig implements JsonParser {

  public int spamLimit;
  public String chatFormat;
  public String defaultChannel;
  public String messageFormat;

  public LanguageConfig() {
    this.spamLimit = 3;
    this.chatFormat = "%CHANNEL% %PREFIX% %NAME% \u00BB%SUFFIX% %MESSAGE%";
    this.defaultChannel = "global";
    this.messageFormat = "[%PLAYER% -> %PLAYER2%] %MESSAGE%";
  }

  public LanguageConfig(int spamLimit, String chatFormat, String defaultChannel) {
    this.spamLimit = spamLimit;
    this.chatFormat = chatFormat;
    this.defaultChannel = defaultChannel;
  }

  @Override
  public String getID() {
    return "Language";
  }
}
