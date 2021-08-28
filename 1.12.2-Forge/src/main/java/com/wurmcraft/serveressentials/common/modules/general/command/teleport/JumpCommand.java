package com.wurmcraft.serveressentials.common.modules.general.command.teleport;

import com.wurmcraft.serveressentials.api.command.Command;
import com.wurmcraft.serveressentials.api.command.ModuleCommand;
import com.wurmcraft.serveressentials.api.models.ServerPlayer;
import com.wurmcraft.serveressentials.api.models.local.Location;
import com.wurmcraft.serveressentials.common.utils.ChatHelper;
import com.wurmcraft.serveressentials.common.utils.TeleportUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;

@ModuleCommand(module = "General", name = "Jump")
public class JumpCommand {

    @Command(args = {}, usage = {})
    public void jump(ServerPlayer player) {
        RayTraceResult result = player.player.rayTrace(128,0);
        if(result != null && result.typeOfHit != RayTraceResult.Type.MISS) {
            Location location = new Location(result.hitVec.x, result.hitVec.y, result.hitVec.z,player.player.dimension, player.player.rotationPitch,player.player.rotationYaw);
            if(TeleportUtils.teleportTo((EntityPlayerMP) player.player,player.local, location))
                ChatHelper.send(player.sender,player.lang.COMMAND_JUMP);
        } else
            ChatHelper.send(player.sender, player.lang.COMMAND_JUMP_MISS);
    }
}
