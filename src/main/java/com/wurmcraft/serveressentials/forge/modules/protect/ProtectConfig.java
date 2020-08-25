package com.wurmcraft.serveressentials.forge.modules.protect;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;
import com.wurmcraft.serveressentials.forge.api.module.ConfigModule;

@ConfigModule(moduleName = "Protect")
public class ProtectConfig implements JsonParser {

  public int explosionCheckResolution;
  public int explosionExpandedRadius;
  public String defaultClaimingStyle;
  public String claimType;
  public int minimumClaimAmount;

  public ProtectConfig() {
    this.explosionCheckResolution = 4;
    this.explosionExpandedRadius = 64;
    this.minimumClaimAmount = 144;
    this.defaultClaimingStyle = "Item";
    this.claimType = "2D";
  }

  public ProtectConfig(int explosionCheckResolution, int explosionExpandedRadius,
      String defaultClaimingStyle, String claimType, int minimumClaimAmount) {
    this.explosionCheckResolution = explosionCheckResolution;
    this.explosionExpandedRadius = explosionExpandedRadius;
    this.defaultClaimingStyle = defaultClaimingStyle;
    this.claimType = claimType;
    this.minimumClaimAmount = minimumClaimAmount;
  }

  @Override
  public String getID() {
    return "Protect";
  }
}
