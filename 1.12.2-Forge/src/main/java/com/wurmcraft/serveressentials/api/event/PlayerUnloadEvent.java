package com.wurmcraft.serveressentials.api.event;

import com.wurmcraft.serveressentials.api.models.Account;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PlayerUnloadEvent extends Event {

    public Account account;

    public PlayerUnloadEvent(Account account) {
        this.account = account;
    }
}
