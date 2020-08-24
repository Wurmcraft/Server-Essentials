package com.wurmcraft.serveressentials.forge.modules.protect.data;

import net.minecraft.util.math.BlockPos;

public class ClaimData {

  public String owner;

  public long startX;
  public long endX;
  public long startY;
  public long endY;
  public long startZ;
  public long endZ;

  public ClaimData(String uuid, long startX, long endX, long startY, long endY,
      long startZ, long endZ) {
    this.owner = uuid;
    this.startX = startX;
    this.endX = endX;
    this.startY = startY;
    this.endY = endY;
    this.startZ = startZ;
    this.endZ = endZ;
  }

  public ClaimData(String uuid, BlockPos startPos, BlockPos endPos) {
    this.owner = uuid;
    this.startX = startPos.getX();
    this.endX = endPos.getX();
    this.startY = startPos.getY();
    this.endY = endPos.getY();
    this.startZ = startPos.getZ();
    this.endZ = endPos.getZ();
  }
}
