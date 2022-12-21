package com.wurmcraft.serveressentials.api.models.local;

public class Location {

  public double x;
  public double y;
  public double z;
  public int dim;
  public double pitch;
  public double yaw;

  public Location(double x, double y, double z, int dim, double pitch, double yaw) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.dim = dim;
    this.pitch = pitch;
    this.yaw = yaw;
  }

  public Location() {

  }
}
