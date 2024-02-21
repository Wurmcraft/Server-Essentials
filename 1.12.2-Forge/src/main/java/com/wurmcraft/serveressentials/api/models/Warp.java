package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.models.local.Home;
import com.wurmcraft.serveressentials.api.models.local.Location;

public class Warp extends Location {

  public String name;

  public Warp(double x, double y, double z, int dim, double pitch, double yaw, String name) {
    super(x, y, z, dim, pitch, yaw);
    this.name = name;
  }

  public Warp() {
  }
}
