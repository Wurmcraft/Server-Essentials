package com.wurmcraft.serveressentials.forge.api.json.player;

import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;

public class Home extends LocationWrapper {

  public String name;

  public Home(String name, double x, double y, double z, int dim) {
    super(x, y, z, dim);
    this.name = name;
  }

  @Override
  public String getID() {
    return name;
  }
}