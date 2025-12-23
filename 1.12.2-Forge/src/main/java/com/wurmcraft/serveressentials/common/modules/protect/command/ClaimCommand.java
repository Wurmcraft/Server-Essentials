package com.wurmcraft.serveressentials.common.modules.protect.command;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.CommandArgument;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.common.modules.protect.models.Claim;
import com.wurmcraft.serveressentials.common.modules.protect.models.Position;
import com.wurmcraft.serveressentials.common.modules.protect.models.TrustInfo;
import com.wurmcraft.serveressentials.common.modules.protect.utils.ProtectionHelper;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import java.util.HashMap;
import net.minecraft.util.math.BlockPos;

@ModuleCommand(
    module = "Claim",
    name = "Claim",
    defaultAliases = {"Storage"})
public class ClaimCommand {

  @Command(
      args = {},
      usage = {})
  public void claim(ServerPlayer player) {
    claimRadius(player, 0);
  }

  @Command(
      args = {CommandArgument.INTEGER},
      usage = {"radius"})
  public void claimRadius(ServerPlayer player, int radius) {
    if (!ProtectionHelper.isClaimed(
        new BlockPos(player.player.posX, player.player.posY, player.player.posZ),
        player.player.dimension,
        radius,
        8,
        player.player.world.isOutsideBuildHeight(new BlockPos(0, 257, 0)))) {
      Claim claim =
          new Claim(
              player.player.getGameProfile().getId().toString(),
              new TrustInfo[] {},
              new Position(
                  player.player.chunkCoordX - radius,
                  player.player.chunkCoordY - radius,
                  player.player.chunkCoordZ - radius),
              new Position(
                  player.player.chunkCoordX + radius,
                  player.player.chunkCoordY + radius,
                  player.player.chunkCoordZ + radius),
              new HashMap<>(),
              Claim.ClaimType.BASIC,
              new HashMap<>());
      ProtectionHelper.newClaim(claim, player.player.dimension);
      ChatHelper.send(player.sender, player.lang.COMMAND_CLAIM_NEW);
    } else {
      ChatHelper.send(player.sender, player.lang.COMMAND_CLAIM_CLAIMED);
    }
  }
}
