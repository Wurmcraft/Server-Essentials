package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import com.wurmcraft.serveressentials.common.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayerMP;

@ModuleCommand(
    module = "General",
    name = "Top",
    defaultAliases = {"Surface"})
public class TopCommand {

  @Command(
      args = {},
      usage = {})
  public void top(ServerPlayer player) {
    int maxHeight = player.player.world.getHeight();
    if (maxHeight <= 256) {
      if (TeleportUtils.teleportTo(
          (EntityPlayerMP) player.player,
          player.local,
          new Location(
              player.player.posX,
              WorldUtils.findTop(
                  player.player.world, (int) player.player.posX, (int) player.player.posZ),
              player.player.posZ,
              player.player.dimension,
              player.player.rotationPitch,
              player.player.rotationYaw))) {
        ChatHelper.send(player.sender, player.lang.COMMAND_TOP);
      }
    } else {
      for (int y = (int) player.player.posY; y < player.player.posY + 256; y++) {
        if (RTPCommand.isSafeLocation(
                player.player.world, (int) player.player.posX, y, (int) player.player.posZ)
            && TeleportUtils.teleportTo(
                (EntityPlayerMP) player.player,
                player.local,
                new Location(
                    player.player.posX,
                    WorldUtils.findTop(
                        player.player.world, (int) player.player.posX, (int) player.player.posZ),
                    player.player.posZ,
                    player.player.dimension,
                    player.player.rotationPitch,
                    player.player.rotationYaw))) {
          ChatHelper.send(player.sender, player.lang.COMMAND_TOP);
          break;
        }
      }
    }
  }
}
