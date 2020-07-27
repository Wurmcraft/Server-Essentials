package com.wurmcraft.serveressentials.fabric.api.json.basic;

import com.wurmcraft.serveressentials.fabric.api.json.player.Home;

public class Warp extends Home {

  public Warp() {
    super("", 0, 0, 0, 0);
  }

  public Warp(String name, double x, double y, double z, int dim) {
    super(name, x, y, z, dim);
  }
}
