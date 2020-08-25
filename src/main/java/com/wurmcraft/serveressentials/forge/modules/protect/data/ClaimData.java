package com.wurmcraft.serveressentials.forge.modules.protect.data;

import net.minecraft.util.math.BlockPos;

public class ClaimData {

  public String owner;
  public String[] trusted;
  public Pos lowerPos;
  public Pos higherPos;
  public Settings settings;
  public ClaimType claimType;
  public String claimTypeData;

  public ClaimData(String owner, String[] trusted,
      Pos startX,
      Pos endX,
      Settings settings) {
    this.owner = owner;
    this.trusted = trusted;
    this.lowerPos = startX;
    this.higherPos = endX;
    this.settings = settings;
    this.claimType = ClaimType.NORMAL;
    this.claimTypeData = "";
  }

  public ClaimData(String owner, String[] trusted,
      BlockPos startX,
      BlockPos endX, Settings settings) {
    this.owner = owner;
    this.trusted = trusted;
    this.lowerPos = new Pos(startX);
    this.higherPos = new Pos(endX);
    this.settings = settings;
    this.claimType = ClaimType.NORMAL;
    this.claimTypeData = "";
  }

  public static class Pos {

    public long x;
    public long y;
    public long z;

    public Pos(long x, long y, long z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public Pos(BlockPos pos) {
      this.x = pos.getX();
      this.y = pos.getY();
      this.z = pos.getZ();
    }

    public BlockPos toBlockPos() {
      return new BlockPos(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Pos) {
        return ((Pos) obj).x == x && ((Pos) obj).y == y && ((Pos) obj).z == z;
      }
      return false;
    }
  }

  public static class Settings {

    public String[] allowedActions;

    public Settings(String[] actions) {
      this.allowedActions = actions;
    }

    public Settings() {
      this.allowedActions = new String[0];
    }
  }

  public enum ClaimType {
    NORMAL, RENT
  }
}
