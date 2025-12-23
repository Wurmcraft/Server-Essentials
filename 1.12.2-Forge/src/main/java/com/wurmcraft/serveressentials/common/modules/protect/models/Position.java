package com.wurmcraft.serveressentials.common.modules.protect.models;

public class Position {

  public int x;
  public int y;
  public int z;

  public Position(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Position) {
      Position p = (Position) obj;
      return (x == p.x) && (y == p.y) && (z == p.z);
    }
    return false;
  }
}
