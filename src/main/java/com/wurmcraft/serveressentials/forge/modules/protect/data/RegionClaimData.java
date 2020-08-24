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
    public long z;
    public long y;
    public int dim;

    public RegionPos(long x, long z, long y) {
      this.x = x;
      this.z = z;
      this.y = y;
      this.dim = 0;
    }

    public RegionPos(long x, long z, long y, int dim) {
      this.x = x;
      this.z = z;
      this.y = y;
      this.dim = dim;
    }
  }

  @Override
  public String getID() {
    return regionPos.x + "-" + regionPos.y + "-" + regionPos.z + "_" + regionPos.dim;
  }
}
