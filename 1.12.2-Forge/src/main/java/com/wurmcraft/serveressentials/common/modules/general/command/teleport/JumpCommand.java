package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.command.CommandUtils;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@ModuleCommand(module = "General", name = "Jump")
public class JumpCommand {

    @Command(args = {}, usage = {})
    public void jump(ServerPlayer player) {
        BlockPos pos = CommandUtils.getRayTracedPos(player.player, 128);
        if (pos != null) {
            Location location = new Location(pos.getX(), pos.getY(),pos.getZ(), player.player.dimension, player.player.rotationPitch, player.player.rotationYaw);
            if (TeleportUtils.teleportTo((EntityPlayerMP) player.player, player.local, location))
                ChatHelper.send(player.sender, player.lang.COMMAND_JUMP);
        } else
            ChatHelper.send(player.sender, player.lang.COMMAND_JUMP_MISS);
    }
}
