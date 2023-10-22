package com.wurmcraft.serveressentials.common.modules.protect.models;

public class RegionClaim {

  protected String regionID;
  public RegionPos regionPos;
  public Claim[] claims;

  public RegionClaim(String regionID, RegionPos regionPos, Claim[] claims) {
    this.regionID = regionID;
    this.regionPos = regionPos;
    this.claims = claims;
  }

  public RegionClaim() {}
}
