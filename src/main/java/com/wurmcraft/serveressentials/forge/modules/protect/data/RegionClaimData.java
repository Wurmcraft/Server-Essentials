package com.wurmcraft.serveressentials.forge.modules.protect.data;

import com.wurmcraft.serveressentials.forge.api.json.JsonParser;

public class RegionClaimData implements JsonParser {

  public RegionPos regionPos;
  public ClaimData[] claims;

  public RegionClaimData(
      RegionPos regionPos,
      ClaimData[] claims) {
    this.regionPos = regionPos;
    this.claims = claims;
  }

  public static class RegionPos {

    public long x;
    public long y;
    public long z;
    public int dim;

    public RegionPos(long x, long y, long z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.dim = 0;
    }

    public RegionPos(long x, long y, long z, int dim) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.dim = dim;
    }
  }

  @Override
  public String getID() {
    return regionPos.x + "-" + regionPos.y + "-" + regionPos.z + "_" + regionPos.dim;
  }
}
