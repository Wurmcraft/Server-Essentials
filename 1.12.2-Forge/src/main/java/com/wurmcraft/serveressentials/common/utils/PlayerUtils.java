package com.wurmcraft.serveressentials.common.utils;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PlayerUtils {

    public static boolean isUserOnline(String uuid) {
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            if (player.getGameProfile().getId().toString().equals(uuid))
                return true;
        return false;
    }
}
