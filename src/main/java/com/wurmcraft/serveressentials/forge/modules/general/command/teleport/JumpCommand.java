package com.wurmcraft.serveressentials.forge.modules.general.command.teleport;

import com.wurmcraft.serveressentials.forge.api.command.Command;
import com.wurmcraft.serveressentials.forge.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.forge.api.json.basic.LocationWrapper;
import com.wurmcraft.serveressentials.forge.server.utils.ChatHelper;
import com.wurmcraft.serveressentials.forge.server.utils.PlayerUtils;
import com.wurmcraft.serveressentials.forge.server.utils.TeleportUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

@ModuleCommand(moduleName = "General", name = "jump")
public class JumpCommand {

  public static final int MAX_TRACE = 64;

  @Command(inputArguments = {})
  public void trace(ICommandSender sender) {
    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      BlockPos ray = getRayTracedPos(player, MAX_TRACE);
      TeleportUtils.teleportTo(player,
          new LocationWrapper(ray.getX(), ray.getY(), ray.getZ(),
              player.dimension));
      ChatHelper.sendMessage(sender, PlayerUtils.getLanguage(sender).GENERAL_JUMP);
    }
  }

  public static BlockPos getRayTracedPos(EntityPlayer player, int distance) {
    Vec3d lookAt = player.getLook(1);
    Vec3d pos =
        new Vec3d(
            player.posX,
            player.posY + (player.getEyeHeight() - player.getDefaultEyeHeight()),
            player.posZ);
    Vec3d a = pos.addVector(0, player.getEyeHeight(), 0);
    Vec3d b = a.addVector(lookAt.x * distance, lookAt.y * distance, lookAt.z * distance);
    RayTraceResult result = player.world.rayTraceBlocks(a, b);
    if (player.world.rayTraceBlocks(a, b) != null && Type.BLOCK
        .equals(result.typeOfHit)) {
      return new BlockPos(result.hitVec.x, result.hitVec.y, result.hitVec.z);
    }
    return player.getPosition();
  }
}
