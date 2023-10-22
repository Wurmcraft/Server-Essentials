package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.SECore;
import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.modules.general.ConfigGeneral;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.PlayerUtils;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import com.wurmcraft.serveressentials.common.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@ModuleCommand(module = "General", name = "RTP")
public class RTPCommand {

  @Command(
      args = {},
      usage = {})
  public void rtp(ServerPlayer player) {
    int radius = ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).rtpRadius;
    String[] blacklist = ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).rtpBiomeBlacklist;
    Location spawn = PlayerUtils.getSpawn(player.global.rank);
    World world = player.player.world;
    if (spawn == null) {
      spawn =
          new Location(
              world.getSpawnPoint().getX(),
              world.getSpawnPoint().getY(),
              world.getSpawnPoint().getZ(),
              0,
              0,
              0);
    }
    top:
    while (true) {
      double x = world.rand.nextInt(radius);
      double z = world.rand.nextInt(radius);
      if (world.rand.nextBoolean()) {
        x = spawn.x + x;
      } else {
        x = spawn.x - x;
      }
      if (world.rand.nextBoolean()) {
        z = spawn.z + z;
      } else {
        z = spawn.z - z;
      }
      int y = WorldUtils.findTop(world, (int) x, (int) z);
      if (isSafeLocation(world, (int) x, y, (int) z)) {
        Biome biome = player.player.world.getBiome(new BlockPos(x, spawn.y, z));
        String biomeName = WorldUtils.getBiomeName(biome);
        // Check for blacklist
        for (String list : blacklist) {
          if (list.equalsIgnoreCase(biomeName)) {
            continue top;
          }
        }
        // Valid Location
        boolean valid = false;
        for (int validDim :
            ((ConfigGeneral) SECore.moduleConfigs.get("GENERAL")).rtpDimensionWhitelist) {
          if (player.player.dimension == validDim) {
            valid = true;
          }
        }
        if (!valid) {
          ChatHelper.send(player.sender, player.lang.COMMAND_RTP_DIM);
          return;
        }
        if (TeleportUtils.teleportTo(
            (EntityPlayerMP) player.player,
            player.local,
            new Location(
                x,
                y,
                z,
                player.player.dimension,
                player.player.rotationPitch,
                player.player.rotationYaw))) {
          ChatHelper.send(player.sender, player.lang.COMMAND_RTP);
          return;
        }
        break;
      }
    }
  }

  public static boolean isSafeLocation(World world, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    return world.getBlockState(pos).getBlock().equals(Blocks.AIR)
        && world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR)
        && !world.getBlockState(pos.down()).getBlock().equals(Blocks.AIR)
        && world.getBlockState(pos.down()).isBlockNormalCube();
  }
}
