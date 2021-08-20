package com.wurmcraft.serveressentials.common.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldUtils {

    public static int findTop(World world, int x, int z) {
        int y = world.getHeight(x, z);
        if (world.getBlockState(new BlockPos(x, y, z)).getBlock().equals(Blocks.AIR))
            return y;
        BlockPos tempPos = new BlockPos(x, world.getHeight(), z);
        IBlockState state = Blocks.AIR.getDefaultState();
        while (state.getBlock().equals(Blocks.AIR)) {
            tempPos = tempPos.add(0, -1, 0);
            state = world.getBlockState(tempPos);
        }
        return tempPos.getY() + 1;

    }
}
