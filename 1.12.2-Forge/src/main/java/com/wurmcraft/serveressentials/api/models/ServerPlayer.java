package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import net.minecraft.entity.player.EntityPlayer;

public class ServerPlayer {

    public EntityPlayer player;
    public LocalAccount local;
    public Account global;

    public ServerPlayer(EntityPlayer player, LocalAccount local, Account global) {
        this.player = player;
        this.local = local;
        this.global = global;
    }
}
