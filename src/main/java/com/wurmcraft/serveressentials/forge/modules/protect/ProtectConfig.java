package com.wurmcraft.serveressentials.forge.modules.protect;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Protect")
public class ProtectConfig implements JsonParser {

  public int explosionCheckResolution;
  public int explosionExpandedRadius;

  public ProtectConfig() {
    this.explosionCheckResolution = 4;
    this.explosionExpandedRadius = 64;
  }

  public ProtectConfig(int explosionCheckResolution, int explosionExpandedRadius) {
    this.explosionCheckResolution = explosionCheckResolution;
    this.explosionExpandedRadius = explosionExpandedRadius;
  }

  @Override
  public String getID() {
    return "Protect";
  }
}
