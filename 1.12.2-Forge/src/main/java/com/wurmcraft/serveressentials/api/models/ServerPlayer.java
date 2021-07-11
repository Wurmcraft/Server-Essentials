package com.wurmcraft.serveressentials.api.models;

import com.wurmcraft.serveressentials.api.models.local.LocalAccount;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class ServerPlayer {

    public ICommandSender sender;
    public EntityPlayer player;
    public LocalAccount local;
    public Account global;

    public ServerPlayer(ICommandSender sender) {
        this.sender = sender;
        this.player = null;
        this.local = null;
        this.global = null;
    }

    public ServerPlayer(EntityPlayer player, LocalAccount local, Account global) {
        this.player = player;
        this.local = local;
        this.global = global;
    }
}
