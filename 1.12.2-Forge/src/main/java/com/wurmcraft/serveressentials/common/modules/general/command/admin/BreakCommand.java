package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@ModuleCommand(module = "General", name = "Break")
public class BreakCommand {

  @Command(
      args = {},
      usage = {})
  public void breakBlock(ServerPlayer player) {
    BlockPos pos = CommandUtils.getRayTracedPos(player.player, 160);
    if (pos != null) {
      player.player.world.setTileEntity(pos, null);
      player.player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
      ChatHelper.send(player.sender, player.lang.COMMAND_BREAK);
    } else {
      ChatHelper.send(player.sender, player.lang.COMMAND_JUMP_MISS);
    }
  }

  @Command(
      args = {},
      usage = {},
      isSubCommand = true,
      subCommandAliases = {"Connected", "Similar"})
  public void multi(ServerPlayer player) {
    BlockPos pos = CommandUtils.getRayTracedPos(player.player, 160);
    if (pos != null) {
      Block block = player.player.world.getBlockState(pos).getBlock();
      destroyConnected(player.player.world, block, pos, 0);
      ChatHelper.send(player.sender, player.lang.COMMAND_BREAK_MULTI);
    } else {
      ChatHelper.send(player.sender, player.lang.COMMAND_JUMP_MISS);
    }
  }

  private static void destroyConnected(
      World world, Block block, BlockPos blockPos, int currentDepth) {
    if (currentDepth
        > 128) // Prevent deleting of the entire stone layer of the map (aka crash server)
    {
      return;
    }
    world.setTileEntity(blockPos, null);
    world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
    if (!world.isAirBlock(blockPos.north())
        && world
            .getBlockState(blockPos.north())
            .getBlock()
            .getUnlocalizedName()
            .equalsIgnoreCase(block.getUnlocalizedName())) {
      destroyConnected(world, block, blockPos.north(), ++currentDepth);
    }
    if (!world.isAirBlock(blockPos.south())
        && world
            .getBlockState(blockPos.south())
            .getBlock()
            .getUnlocalizedName()
            .equalsIgnoreCase(block.getUnlocalizedName())) {
      destroyConnected(world, block, blockPos.south(), ++currentDepth);
    }
    if (!world.isAirBlock(blockPos.east())
        && world
            .getBlockState(blockPos.east())
            .getBlock()
            .getUnlocalizedName()
            .equalsIgnoreCase(block.getUnlocalizedName())) {
      destroyConnected(world, block, blockPos.east(), ++currentDepth);
    }
    if (!world.isAirBlock(blockPos.west())
        && world
            .getBlockState(blockPos.west())
            .getBlock()
            .getUnlocalizedName()
            .equalsIgnoreCase(block.getUnlocalizedName())) {
      destroyConnected(world, block, blockPos.west(), ++currentDepth);
    }
    if (!world.isAirBlock(blockPos.up())
        && world
            .getBlockState(blockPos.up())
            .getBlock()
            .getUnlocalizedName()
            .equalsIgnoreCase(block.getUnlocalizedName())) {
      destroyConnected(world, block, blockPos.up(), ++currentDepth);
    }
    if (!world.isAirBlock(blockPos.down())
        && world
            .getBlockState(blockPos.down())
            .getBlock()
            .getUnlocalizedName()
            .equalsIgnoreCase(block.getUnlocalizedName())) {
      destroyConnected(world, block, blockPos.down(), ++currentDepth);
    }
  }
}
