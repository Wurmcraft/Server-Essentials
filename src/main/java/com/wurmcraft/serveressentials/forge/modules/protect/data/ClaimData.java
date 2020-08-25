package com.wurmcraft.serveressentials.forge.modules.protect.data;

import com.wurmcraft.serveressentials.forge.modules.protect.utils.ProtectionHelper.Action;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.BlockPos;

public class ClaimData {

  public String owner;
  public String[] trusted;
  public Pos lowerPos;
  public Pos higherPos;
  public Settings settings;
  public ClaimType claimType;

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
  }

  public static class Settings {

    public Map<Action, String> actions;

    public Settings(
        Map<Action, String> actions) {
      this.actions = actions;
    }

    public Settings() {
      this.actions = new HashMap<>();
      for (Action a : Action.values()) {
        actions.put(a, "true");
      }
    }
  }

  public enum ClaimType {
    NORMAL, RENT
  }
}
