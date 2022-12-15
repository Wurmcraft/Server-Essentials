package com.wurmcraft.serveressentials.common.modules.protect.models;

import com.wurmcraft.serveressentials.common.modules.protect.utils.RegionHelper;

public class RegionClaims {

  protected String id;
  public Position regionPos;
  public Claim[] claims;

  public RegionClaims(Position regionPos, Claim[] claims) {
    this.id = RegionHelper.convertToID(regionPos);
    this.regionPos = regionPos;
    this.claims = claims;
  }
}
