package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.models.local.Location;

public class LastPos {

  public Location location;
  public int checker;

  public LastPos(Location location, int checker) {
    this.location = location;
    this.checker = checker;
  }

  public LastPos(Location location) {
    this.location = location;
    this.checker = 0;
  }

  public void increment() {
    checker++;
  }
}
