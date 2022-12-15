package com.wurmcraft.serveressentials.common.modules.protect.models;

public class RegionPos {

  public int x;
  public int y;  // Cubic, 3d claims, etc..
  public int z;
  public int dim;

  public RegionPos(int x, int y, int z, int dim) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.dim = dim;
  }
}
