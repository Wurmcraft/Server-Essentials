package com.wurmcraft.serveressentials.common.modules.protect.models;

public class RegionClaim {

  protected String regionID;
  public Position regionPos;
  public Claim[] claims;

  public RegionClaim(String regionID, Position regionPos, Claim[] claims) {
    this.regionID = regionID;
    this.regionPos = regionPos;
    this.claims = claims;
  }

  public RegionClaim() {
  }
}
