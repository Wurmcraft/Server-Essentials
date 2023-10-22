package com.wurmcraft.serveressentials.common.modules.protect.models;

import java.util.Map;

public class Claim {

  public String owner;
  public TrustInfo[] trust;
  public Position min;
  public Position max;
  public Map<ClaimSettings, String> settings;
  public ClaimType type;
  public Map<String, String> claimInfo;

  public Claim(
      String owner,
      TrustInfo[] trust,
      Position min,
      Position max,
      Map<ClaimSettings, String> settings,
      ClaimType type,
      Map<String, String> claimInfo) {
    this.owner = owner;
    this.trust = trust;
    this.min = min;
    this.max = max;
    this.settings = settings;
    this.type = type;
    this.claimInfo = claimInfo;
  }

  public enum ClaimSettings {
    DEFAULTS
  }

  public enum ClaimType {
    BASIC,
    RENT,
    ADVANCED,
    SUB
  }
}
