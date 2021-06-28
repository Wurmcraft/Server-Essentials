package com.wurmcraft.serveressentials.api.event;

import com.wurmcraft.serveressentials.api.models.Account;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerLoadEvent extends Event {

    public EntityPlayer player;
    public Account account;
    public boolean newAccount;

    public PlayerLoadEvent(EntityPlayer player, Account account, boolean newAccount) {
        this.player = player;
        this.account = account;
        this.newAccount = newAccount;
    }
}
