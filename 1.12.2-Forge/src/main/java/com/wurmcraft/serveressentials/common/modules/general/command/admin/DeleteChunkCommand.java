package com.wurmcraft.serveressentials.common.modules.general.command.admin;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@ModuleCommand(module = "General", name = "DeleteChunk")
public class DeleteChunkCommand {

  @Command(
      args = {CommandArgument.INTEGER, CommandArgument.INTEGER},
      usage = {"x", "z"})
  public void deleteChunk(ServerPlayer player, int x, int z) {
    World world = player.player.world;
    if (world.getChunkProvider().isChunkGeneratedAt(x, z)) {
      // Set Chunk to air
      Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
      BlockPos chunkPos = new BlockPos(chunk.x * 16, 255, chunk.z * 16);
      for (int posX = 0; posX < 16; posX++) {
        for (int posZ = 0; posZ < 16; posZ++) {
          for (int y = -255; y < 0; y++) {
            BlockPos pos = chunkPos.add(posX, y, posZ);
            world.setTileEntity(pos, null);
            world.setBlockToAir(pos);
          }
        }
      }
    }
    ChatHelper.send(
        player.sender,
        player
            .lang
            .COMMAND_DELETECHUNK
            .replaceAll("\\{@X@}", x + "")
            .replaceAll("\\{@Z@}", z + ""));
  }
}
