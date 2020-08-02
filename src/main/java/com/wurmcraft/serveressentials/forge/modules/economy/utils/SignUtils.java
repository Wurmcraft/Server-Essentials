package com.wurmcraft.serveressentials.forge.modules.economy.utils;

import java.util.Arrays;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SignUtils {

  public static boolean isValidSign(World world, BlockPos pos) {
    if (world.getBlockState(pos).getBlock().equals(Blocks.WALL_SIGN) || world
        .getBlockState(pos).getBlock().equals(Blocks.STANDING_SIGN)) {
      TileEntitySign sign = (TileEntitySign) world.getTileEntity(pos);
      for (SignType type : SignType.values()) {
        String signText = sign.signText[0].getUnformattedText();
        if (type.lineValue.equalsIgnoreCase( sign.signText[0].getUnformattedComponentText())) {
          try {
             Double.parseDouble(sign.signText[3].getUnformattedText());
             return true;
          } catch (NumberFormatException e) {
            return false;
          }
        }
      }
      return false;
    }
    return false;
  }

  public static SignType getType(World world, BlockPos pos) {
    TileEntitySign sign = (TileEntitySign) world.getTileEntity(pos);
    for (SignType type : SignType.values()) {
      if (type.lineValue.equalsIgnoreCase(sign.signText[0].getUnformattedComponentText())) {
        return type;
      }
    }
    return null;
  }


  public enum SignType {
    ADMIN_SELL("[ISell]"), ADMIN_BUY("[IBuy]"),
    SELL("[Sell]"), Buy("[Buy]"),
    COMMAND("Command");

    public String lineValue;

    SignType(String lineValue) {
      this.lineValue = lineValue;
    }
  }

}
