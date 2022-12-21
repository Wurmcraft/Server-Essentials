package com.wurmcraft.serveressentials.api.models.local;

public class Home extends Location {

  public String name;

  public Home(double x, double y, double z, int dim, double pitch, double yaw, String name) {
    super(x, y, z, dim, pitch, yaw);
    this.name = name;
  }

  public Home() {
    super();
  }
}
